package com.example.eventsourcing.repository

import com.example.eventsourcing.domain.event.EventSubscriptionCheckpoint
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.math.BigInteger
import java.sql.ResultSet
import java.util.*

class EventSubscriptionRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {
    fun createSubscriptionIfAbsent(subscriptionName: String) {
        jdbcTemplate.update(
            """
                        INSERT INTO ES_EVENT_SUBSCRIPTION (SUBSCRIPTION_NAME, LAST_TRANSACTION_ID, LAST_EVENT_ID)
                        VALUES (:subscriptionName, '0'::xid8, 0)
                        ON CONFLICT DO NOTHING
                        
                        """.trimIndent(),
            mapOf("subscriptionName" to subscriptionName)
        )
    }

    fun readCheckpointAndLockSubscription(subscriptionName: String): Optional<EventSubscriptionCheckpoint> {
        return jdbcTemplate.query(
            """
                        SELECT LAST_TRANSACTION_ID::text,
                               LAST_EVENT_ID
                          FROM ES_EVENT_SUBSCRIPTION
                         WHERE SUBSCRIPTION_NAME = :subscriptionName
                           FOR UPDATE SKIP LOCKED
                        
                        """.trimIndent(),
            mapOf("subscriptionName" to subscriptionName)
        ) { rs: ResultSet, rowNum: Int -> toEventSubscriptionCheckpoint(rs, rowNum) }
            .stream().findFirst()
    }

    fun updateEventSubscription(
        subscriptionName: String,
        lastProcessedTransactionId: BigInteger,
        lastProcessedEventId: Long
    ): Boolean {
        val updatedRows = jdbcTemplate.update(
            """
                        UPDATE ES_EVENT_SUBSCRIPTION
                           SET LAST_TRANSACTION_ID = :lastProcessedTransactionId::xid8,
                               LAST_EVENT_ID = :lastProcessedEventId
                         WHERE SUBSCRIPTION_NAME = :subscriptionName
                        
                        """.trimIndent(),
            mapOf(
                "subscriptionName" to subscriptionName,
                "lastProcessedTransactionId" to lastProcessedTransactionId.toString(),
                "lastProcessedEventId" to lastProcessedEventId
            )
        )
        return updatedRows > 0
    }

    private fun toEventSubscriptionCheckpoint(rs: ResultSet, rowNum: Int): EventSubscriptionCheckpoint {
        val lastProcessedTransactionId = rs.getString("LAST_TRANSACTION_ID")
        val lastProcessedEventId = rs.getLong("LAST_EVENT_ID")
        return EventSubscriptionCheckpoint(BigInteger(lastProcessedTransactionId), lastProcessedEventId)
    }
}
