package com.example.eventsourcing.repository;

import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventType;
import com.example.eventsourcing.domain.event.EventWithId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public EventRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public <T extends Event> EventWithId<T> appendEvent(Event event) {
        try {
            List<EventWithId<T>> result = jdbcTemplate.query("""
                            INSERT INTO ES_EVENT (TRANSACTION_ID, AGGREGATE_ID, VERSION, EVENT_TYPE, JSON_DATA)
                            VALUES(pg_current_xact_id(), :aggregateId, :version, :eventType, :jsonObj::json)
                            RETURNING ID, TRANSACTION_ID::text, EVENT_TYPE, JSON_DATA
                            """,
                    Map.of(
                            "aggregateId", event.getAggregateId(),
                            "version", event.getVersion(),
                            "eventType", event.getEventType().toString(),
                            "jsonObj", objectMapper.writeValueAsString(event)
                    ),
                    this::toEvent);
            return result.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<EventWithId<Event>> readEvents(UUID aggregateId,
                                               @Nullable Integer fromVersion,
                                               @Nullable Integer toVersion) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregateId);
        parameters.addValue("fromVersion", fromVersion, Types.INTEGER);
        parameters.addValue("toVersion", toVersion, Types.INTEGER);

        return jdbcTemplate.query("""
                        SELECT ID,
                               TRANSACTION_ID::text,
                               EVENT_TYPE,
                               JSON_DATA
                          FROM ES_EVENT
                         WHERE AGGREGATE_ID = :aggregateId
                           AND (:fromVersion IS NULL OR VERSION > :fromVersion)
                           AND (:toVersion IS NULL OR VERSION <= :toVersion)
                         ORDER BY VERSION ASC
                        """,
                parameters,
                this::toEvent);
    }

    public List<EventWithId<Event>> readEventsAfterCheckpoint(AggregateType aggregateType,
                                                              BigInteger lastProcessedTransactionId,
                                                              long lastProcessedEventId) {
        return jdbcTemplate.query("""
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
                        """,
                Map.of(
                        "aggregateType", aggregateType.toString(),
                        "lastProcessedTransactionId", lastProcessedTransactionId.toString(),
                        "lastProcessedEventId", lastProcessedEventId
                ),
                this::toEvent);
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> EventWithId<T> toEvent(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("ID");
        String transactionId = rs.getString("TRANSACTION_ID");
        EventType eventType = EventType.valueOf(rs.getString("EVENT_TYPE"));
        PGobject jsonObj = (PGobject) rs.getObject("JSON_DATA");
        String json = jsonObj.getValue();
        try {
            Event event = objectMapper.readValue(json, eventType.getEventClass());
            return new EventWithId<>(id, new BigInteger(transactionId), (T) event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
