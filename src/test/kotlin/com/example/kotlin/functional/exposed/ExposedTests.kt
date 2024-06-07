package com.example.kotlin.functional.exposed

import com.example.eventsourcing.dto.OrderStatus
import com.example.eventsourcing.projection.OrderProjectionEn
import com.example.eventsourcing.projection.WaypointProjectionE
import com.example.kotlin.functional.TestEnvironment
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.time.LocalDateTime
import java.util.*

class ExposedTests {
    @Test
    fun `exposed definitions work properly`() {
        val dataSource = HikariDataSource(TestEnvironment.hikariConfig)

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()

        flyway.migrate()

        TransactionManager.defaultDatabase = Database.connect(dataSource)

        transaction {
            val new = OrderProjectionEn.new(UUID.randomUUID()) {
                riderId = UUID.randomUUID()
                price = ONE
                version = 1
                status = OrderStatus.PLACED
                placedDate = LocalDateTime.now()
            }
            WaypointProjectionE.new(new.id.value) {
                address = "an address"
                latitude = BigDecimal("0.0")
                longitude = BigDecimal("0.0")
            }

            OrderProjectionEn.all().forEach {
                println(it)
            }
        }
    }
}
