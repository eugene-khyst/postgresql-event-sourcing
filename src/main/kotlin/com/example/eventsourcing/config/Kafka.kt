package com.example.eventsourcing.config

import com.example.eventsourcing.TOPIC_ORDER_EVENTS
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.*

object Kafka {
    fun kafkaClient(kafkaBootstrapServers: String): KafkaTemplate<String, String> {
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
        )

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
}
