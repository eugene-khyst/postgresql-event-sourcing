package com.example.kotlin.functional

import com.example.eventsourcing.app
import com.example.eventsourcing.config.IntegrationEventProperties
import com.example.eventsourcing.config.SnapshottingProperties
import com.zaxxer.hikari.HikariConfig
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TestEnvironment {
    val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
        .also {
            it.start()
        }
    val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"))
        .also {
            it.start()
        }
    val appHandler = app(
        kafka.bootstrapServers,
        SnapshottingProperties(true, 10),
        HikariConfig("/hikari.properties"),
        IntegrationEventProperties(true, 1.toDuration(DurationUnit.SECONDS))
    )
    val appServer: Http4kServer = appHandler.asServer(Undertow(8080))
        .also {
            it.start()
        }
}
