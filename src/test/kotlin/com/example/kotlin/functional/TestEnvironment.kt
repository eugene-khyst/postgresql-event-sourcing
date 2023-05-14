package com.example.kotlin.functional

import com.example.eventsourcing.app
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object TestEnvironment {
    val POSTGRES: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
        .also {
            it.start()
        }
    val KAFKA: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"))
        .also {
            it.start()
        }
    val APP: Http4kServer = app(KAFKA.bootstrapServers).asServer(Undertow(8080))
        .also {
            it.start()
        }
}
