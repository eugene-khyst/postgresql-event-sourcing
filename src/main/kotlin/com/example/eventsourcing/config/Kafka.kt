package com.example.eventsourcing.config

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import java.util.*

object Kafka {
    const val TOPIC_ORDER_EVENTS = "order-events"

    fun kafkaClient(kafkaBootstrapServers: String): KafkaTemplate<String, String> {
        createTopics(TOPIC_ORDER_EVENTS)

        val configs = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 1
        )

        return KafkaTemplate(DefaultKafkaProducerFactory(configs))
    }

    private fun createTopics(topic: String): CreateTopicsResult {
        val props = Properties().also {
            it[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        }

        val adminClient = AdminClient.create(props)

        return adminClient.createTopics(
            listOf(
                TopicBuilder
                    .name(topic)
                    .partitions(10)
                    .replicas(1)
                    .build()
            )
        )
    }
}
