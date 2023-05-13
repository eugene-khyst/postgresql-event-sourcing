package com.example.kotlin.functional

import com.example.eventsourcing.app
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

internal abstract class AbstractContainerBaseTest {
    companion object {
        var POSTGRES: PostgreSQLContainer<*>? = null
        var KAFKA: KafkaContainer? = null
        var APP: Http4kServer? = null

        init {
            POSTGRES = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine")).also {
                it.start()
            }
            KAFKA = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2")).also {
                it.start()
            }
            APP = app().asServer(Undertow(8080)).also {
                it.start()
            }
        }

        @DynamicPropertySource
        fun postgresProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { POSTGRES!!.jdbcUrl }
            registry.add("spring.datasource.username") { POSTGRES!!.username }
            registry.add("spring.datasource.password") { POSTGRES!!.password }
        }

        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { KAFKA!!.bootstrapServers }
        }
    }
}
