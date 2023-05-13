package com.example.eventsourcing;

import com.example.eventsourcing.config.EventSourcingProperties
import com.example.eventsourcing.config.EventSourcingProperties.SnapshottingProperties
import com.example.eventsourcing.controller.OrdersController
import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.projection.OrderProjection
import com.example.eventsourcing.repository.AggregateRepository
import com.example.eventsourcing.repository.EventRepository
import com.example.eventsourcing.repository.EventSubscriptionRepository
import com.example.eventsourcing.service.AggregateStore
import com.example.eventsourcing.service.CommandProcessor
import com.example.eventsourcing.service.EventSubscriptionProcessor
import com.example.eventsourcing.service.ScheduledEventSubscriptionProcessor
import com.example.eventsourcing.service.command.DefaultCommandHandler
import com.example.eventsourcing.service.command.PlaceOrderCommandHandler
import com.example.eventsourcing.service.event.OrderIntegrationEventSender
import com.example.eventsourcing.service.event.OrderProjectionUpdater
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.flywaydb.core.Flyway
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
import org.hibernate.jpa.HibernatePersistenceProvider
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.InvocationTargetException
import java.util.*

const val TOPIC_ORDER_EVENTS = "order-events"


private inline fun <reified T : Any> ResponseEntity<T>.toHttp4kResponse() =
    Response(Status(this.statusCode.value(), null))
        .headers(this.headers.map { it.key to it.value.firstOrNull() }.toList()).let {
            if (this.body != null) it.with(Body.auto<T>().toLens() of body) else it
        }

fun kafkaTemplate(kafkaBootstrapServers: String): KafkaTemplate<String, String> {
    val props = Properties()
    props[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"

    val adminClient = AdminClient.create(props)

    val topics: MutableList<NewTopic> = ArrayList()
    topics.add(
        TopicBuilder
            .name(TOPIC_ORDER_EVENTS)
            .partitions(10)
            .replicas(1)
            .build()
    );

    adminClient.createTopics(topics)
    val configs = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 1
    )

    val producerFactory: ProducerFactory<String, String> = DefaultKafkaProducerFactory(configs)
    return KafkaTemplate(producerFactory)
}
val objectMapper: ObjectMapper = jacksonObjectMapper().also {
    it.findAndRegisterModules()
    it.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
    it.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    it.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

fun app(kafkaBootstrapServers: String): RoutingHttpHandler {
    val dataSource = HikariDataSource(HikariConfig("/hikari.properties"))
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration") // Specify the location of your migration files
        .load()

    flyway.migrate()
    val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    val aggregateRepository = AggregateRepository(namedParameterJdbcTemplate, objectMapper)
    val eventRepository = EventRepository(namedParameterJdbcTemplate, objectMapper)
    val aggregateStore = AggregateStore(
        aggregateRepository, eventRepository,
        EventSourcingProperties(mapOf(AggregateType.ORDER to SnapshottingProperties(true, 10)))
    )
    val hibernateProperties = Properties().also {
        it.setProperty("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy::class.java.name)
    }

    val entityManagerFactory = LocalContainerEntityManagerFactoryBean().also { em ->
        em.dataSource = dataSource
        em.setPackagesToScan("com.example.eventsourcing")
        em.jpaVendorAdapter = HibernateJpaVendorAdapter()
        em.persistenceUnitName = "persistence"
        em.setJpaProperties(hibernateProperties)
        em.setPersistenceProviderClass(HibernatePersistenceProvider::class.java)
        em.afterPropertiesSet()
    }.`object`

    val entityManager = entityManagerFactory.createEntityManager()
    val orderProjectionRepository =
        SimpleJpaRepository<OrderProjection, UUID>(OrderProjection::class.java, entityManager)

    val commandProcessor = CommandProcessor(
        aggregateStore,
        listOf(PlaceOrderCommandHandler()),
        DefaultCommandHandler(),
        listOf(OrderProjectionUpdater(orderProjectionRepository))
    )

    val kafkaTemplate = kafkaTemplate(kafkaBootstrapServers)
    val orderIntegrationEventSender = OrderIntegrationEventSender(aggregateStore, kafkaTemplate, objectMapper)
    val eventSubscriptionProcessor =
        EventSubscriptionProcessor(EventSubscriptionRepository(namedParameterJdbcTemplate), eventRepository)

    val scheduledEventSubscriptionProcessor = ScheduledEventSubscriptionProcessor(
        listOf(orderIntegrationEventSender),
        eventSubscriptionProcessor
    )

    GlobalScope.launch {
        while (true) {
            delay(1000) // Delay for 1 second
            scheduledEventSubscriptionProcessor.processNewEvents()
        }
    }

    val ordersController = OrdersController(objectMapper, commandProcessor, orderProjectionRepository)
    val errorMessageLens = Body.auto<ErrorMessage>().toLens()

    val errorHandler = { e: Throwable ->
        when (e) {
            is UnsupportedOperationException -> {
                val cause = e.cause
                when (cause) {
                    is InvocationTargetException -> Response(Status.BAD_REQUEST)
                        .with(errorMessageLens of ErrorMessage((cause.targetException.message ?: "").format(e)))

                    else -> {
                        Response(Status.BAD_REQUEST)
                            .with(errorMessageLens of ErrorMessage((e.message ?: "").format(e)))
                    }
                }
            }

            else -> {
                if (e !is Exception) throw e

                val stackTraceAsString = StringWriter().apply {
                    e.printStackTrace(PrintWriter(this))
                }.toString()

                Response(Status.INTERNAL_SERVER_ERROR).body(stackTraceAsString)
            }
        }
    }
    return ServerFilters.CatchAll(errorHandler).then(
        routes(
            "/orders" bind Method.GET to { request: Request -> ordersController.orders.toHttp4kResponse() },
            "/orders" bind Method.POST to { request: Request ->
                ordersController.placeOrder(
                    objectMapper.readTree(
                        request.bodyString()
                    )
                ).toHttp4kResponse()
            },
            "/orders/{orderId}" bind Method.GET to { request: Request ->
                ordersController.getOrder(
                    UUID.fromString(
                        request.path(
                            "orderId"
                        )
                    )
                ).toHttp4kResponse()
            },
            "/orders/{orderId}" bind Method.PUT to { request: Request ->
                ordersController.modifyOrder(
                    UUID.fromString(
                        request.path(
                            "orderId"
                        )
                    ),
                    objectMapper.readTree(
                        request.bodyString()
                    )
                ).toHttp4kResponse()
            },
        )
    )
}

fun main() {
    val server = app((System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092"))
        .asServer(Undertow(8080))
        .start()
}
