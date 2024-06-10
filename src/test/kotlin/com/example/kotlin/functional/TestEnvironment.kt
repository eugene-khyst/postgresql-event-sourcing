package com.example.kotlin.functional

import com.example.eventsourcing.app
import com.example.eventsourcing.config.IntegrationEventProperties
import com.example.eventsourcing.config.SnapshottingProperties
import com.example.kotlin.fakes.TestClock
import com.zaxxer.hikari.HikariConfig
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.time.Clock
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TestEnvironment {
    val databaseName = "postgres"
    val databaseUsername = "admin"
    val databasePassword = "s3cr3t"
    val postgres: PostgreSQLContainer<*> =
        PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName(databaseName)
            .withUsername(databaseUsername)
            .withPassword(databasePassword).apply { start() }

    val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1")).also {
        it.start()
    }
    val clock = TestClock()

    val hikariConfig = HikariConfig(Properties().apply {
        putAll(
            mapOf(
                "dataSourceClassName" to "org.postgresql.ds.PGSimpleDataSource",
                "dataSource.user" to databaseUsername,
                "dataSource.databaseName" to databaseName,
                "dataSource.password" to databasePassword,
                "dataSource.portNumber" to "${postgres.firstMappedPort}",
                "dataSource.serverName" to "localhost",
                "dataSource.currentSchema" to "testing"
            )
        )
    })
    fun newApp(integrationKafkaTopic: String) = app(
        clock,
        kafka.bootstrapServers,
        SnapshottingProperties(true, 10),
        hikariConfig,
        IntegrationEventProperties(true, 1.toDuration(DurationUnit.SECONDS), integrationKafkaTopic)
    )
}
