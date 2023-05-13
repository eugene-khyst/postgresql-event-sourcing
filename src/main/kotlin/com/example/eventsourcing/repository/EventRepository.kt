package com.example.eventsourcing.repository

import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.domain.event.EventType
import com.example.eventsourcing.domain.event.EventWithId
import com.fasterxml.jackson.databind.ObjectMapper
import org.postgresql.util.PGobject
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.math.BigInteger
import java.sql.ResultSet
import java.sql.Types
import java.util.*

class EventRepository(private val jdbcTemplate: NamedParameterJdbcTemplate, private val objectMapper: ObjectMapper) {
    fun <T : Event> appendEvent(event: Event): EventWithId<T> {
        val result = jdbcTemplate.query<EventWithId<T>>(
            """
                            INSERT INTO ES_EVENT (TRANSACTION_ID, AGGREGATE_ID, VERSION, EVENT_TYPE, JSON_DATA)
                            VALUES(pg_current_xact_id(), :aggregateId, :version, :eventType, :jsonObj::json)
                            RETURNING ID, TRANSACTION_ID::text, EVENT_TYPE, JSON_DATA
                            
                            """.trimIndent(),
            mapOf(
                "aggregateId" to event.aggregateId,
                "version" to event.version,
                "eventType" to event.eventType.toString(),
                "jsonObj" to objectMapper.writeValueAsString(event)
            )
        ) { rs: ResultSet, rowNum: Int -> toEvent(rs, rowNum) }
        return result[0]
    }

    fun readEvents(
        aggregateId: UUID,
        fromVersion: Int?,
        toVersion: Int?
    ): List<EventWithId<Event>> {
        val parameters = MapSqlParameterSource()
        parameters.addValue("aggregateId", aggregateId)
        parameters.addValue("fromVersion", fromVersion, Types.INTEGER)
        parameters.addValue("toVersion", toVersion, Types.INTEGER)
        return jdbcTemplate.query(
            """
                        SELECT ID,
                               TRANSACTION_ID::text,
                               EVENT_TYPE,
                               JSON_DATA
                          FROM ES_EVENT
                         WHERE AGGREGATE_ID = :aggregateId
                           AND (:fromVersion IS NULL OR VERSION > :fromVersion)
                           AND (:toVersion IS NULL OR VERSION <= :toVersion)
                         ORDER BY VERSION ASC
                        
                        """.trimIndent(),
            parameters
        ) { rs: ResultSet, rowNum: Int -> toEvent(rs, rowNum) }
    }

    fun readEventsAfterCheckpoint(
        aggregateType: AggregateType,
        lastProcessedTransactionId: BigInteger,
        lastProcessedEventId: Long
    ): List<EventWithId<Event>> {
        return jdbcTemplate.query(
            """
                        SELECT e.ID,
                               e.TRANSACTION_ID::text,
                               e.EVENT_TYPE,
                               e.JSON_DATA
                          FROM ES_EVENT e
                          JOIN ES_AGGREGATE a on a.ID = e.AGGREGATE_ID
                         WHERE a.AGGREGATE_TYPE = :aggregateType
                           AND (e.TRANSACTION_ID, e.ID) > (:lastProcessedTransactionId::xid8, :lastProcessedEventId)
                           AND e.TRANSACTION_ID < pg_snapshot_xmin(pg_current_snapshot())
                         ORDER BY e.TRANSACTION_ID ASC, e.ID ASC
                        
                        """.trimIndent(),
            mapOf(
                "aggregateType" to aggregateType.toString(),
                "lastProcessedTransactionId" to lastProcessedTransactionId.toString(),
                "lastProcessedEventId" to lastProcessedEventId
            )
        ) { rs: ResultSet, rowNum: Int -> toEvent(rs, rowNum) }
    }

    private fun <T : Event> toEvent(rs: ResultSet, rowNum: Int): EventWithId<T> {
        val id = rs.getLong("ID")
        val transactionId = rs.getString("TRANSACTION_ID")
        val eventType = EventType.valueOf(rs.getString("EVENT_TYPE"))
        val jsonObj = rs.getObject("JSON_DATA") as PGobject
        val json = jsonObj.value
        val event = objectMapper.readValue(json, eventType.eventClass)
        return EventWithId(id, BigInteger(transactionId), event as T)
    }
}
