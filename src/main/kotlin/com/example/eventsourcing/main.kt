package com.example.eventsourcing;

import com.example.eventsourcing.config.EventSourcingProperties
import com.example.eventsourcing.controller.OrdersController
import com.example.eventsourcing.projection.OrderProjection
import com.example.eventsourcing.repository.AggregateRepository
import com.example.eventsourcing.repository.EventRepository
import com.example.eventsourcing.repository.OrderProjectionRepository
import com.example.eventsourcing.service.AggregateStore
import com.example.eventsourcing.service.CommandProcessor
import com.example.eventsourcing.service.command.DefaultCommandHandler
import com.example.eventsourcing.service.command.PlaceOrderCommandHandler
import com.example.eventsourcing.service.event.OrderProjectionUpdater
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
import org.hibernate.jpa.HibernatePersistenceProvider
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.query.FluentQuery
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.function.Function

private inline fun <reified T : Any> ResponseEntity<T>.toHttp4kResponse() =
    Response(Status(this.statusCode.value(), null))
        .headers(this.headers.map { it.key to it.value.firstOrNull() }.toList()).let {
            if (this.body != null) it.with(Body.auto<T>().toLens() of body) else it
        }

val objectMapper: ObjectMapper = jacksonObjectMapper().also {
    it.findAndRegisterModules()
    it.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
    it.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    it.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

fun app(): RoutingHttpHandler {
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
        EventSourcingProperties()
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
    val repository = SimpleJpaRepository<OrderProjection, UUID>(OrderProjection::class.java, entityManager)

    val orderProjectionRepository = object : OrderProjectionRepository {
        override fun <S : OrderProjection?> save(entity: S): S = repository.save(entity)

        override fun <S : OrderProjection?> saveAll(entities: MutableIterable<S>): MutableList<S> =
            repository.saveAll(entities)

        override fun findById(id: UUID): Optional<OrderProjection> = repository.findById(id)

        override fun existsById(id: UUID): Boolean = repository.existsById(id)

        override fun <S : OrderProjection?> findAll(example: Example<S>): MutableList<S> = repository.findAll(example)

        override fun <S : OrderProjection?> findAll(example: Example<S>, sort: Sort): MutableList<S> =
            repository.findAll(example, sort)

        override fun findAll(): MutableList<OrderProjection> = repository.findAll()

        override fun findAll(sort: Sort): MutableList<OrderProjection> = repository.findAll(sort)

        override fun findAll(pageable: Pageable): Page<OrderProjection> = repository.findAll(pageable)

        override fun <S : OrderProjection?> findAll(example: Example<S>, pageable: Pageable): Page<S> =
            repository.findAll(example, pageable)

        override fun findAllById(ids: MutableIterable<UUID>): MutableList<OrderProjection> = repository.findAllById(ids)

        override fun count(): Long = repository.count()

        override fun <S : OrderProjection?> count(example: Example<S>): Long = repository.count(example)

        override fun deleteById(id: UUID) = repository.deleteById(id)

        override fun delete(entity: OrderProjection) = repository.delete(entity)

        override fun deleteAllById(ids: MutableIterable<UUID>) = repository.deleteAllById(ids)

        override fun deleteAll(entities: MutableIterable<OrderProjection>) = repository.deleteAll(entities)

        override fun deleteAll() = repository.deleteAll()

        override fun <S : OrderProjection?> findOne(example: Example<S>): Optional<S> = repository.findOne(example)

        override fun <S : OrderProjection?> exists(example: Example<S>): Boolean = repository.exists(example)

        override fun <S : OrderProjection?, R : Any?> findBy(
            example: Example<S>,
            queryFunction: Function<FluentQuery.FetchableFluentQuery<S>, R>
        ): R = repository.findBy(example, queryFunction)

        override fun flush() = repository.flush()

        override fun <S : OrderProjection?> saveAndFlush(entity: S): S = repository.saveAndFlush(entity)

        override fun <S : OrderProjection?> saveAllAndFlush(entities: MutableIterable<S>): MutableList<S> =
            repository.saveAllAndFlush(entities)

        override fun deleteAllInBatch(entities: MutableIterable<OrderProjection>) =
            repository.deleteAllInBatch(entities)

        override fun deleteAllInBatch() = repository.deleteAllInBatch()

        override fun deleteAllByIdInBatch(ids: MutableIterable<UUID>) = repository.deleteAllByIdInBatch(ids)

        override fun getOne(id: UUID): OrderProjection = repository.getOne(id)

        override fun getById(id: UUID): OrderProjection = repository.getById(id)

        override fun getReferenceById(id: UUID): OrderProjection = repository.getReferenceById(id)

    }

    val commandProcessor = CommandProcessor(
        aggregateStore,
        listOf(PlaceOrderCommandHandler()),
        DefaultCommandHandler(),
        listOf(OrderProjectionUpdater(orderProjectionRepository))
    )

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
    val server = app().asServer(Undertow(9000)).start()

    val client = ApacheClient()

    val request = Request(Method.GET, "http://localhost:9000/orders")

    println(client(request))

    server.stop()
}
