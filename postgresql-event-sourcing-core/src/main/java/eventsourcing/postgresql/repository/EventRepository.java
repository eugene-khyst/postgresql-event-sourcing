package eventsourcing.postgresql.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import eventsourcing.postgresql.domain.event.Event;
import eventsourcing.postgresql.domain.event.EventTypeMapper;
import eventsourcing.postgresql.domain.event.EventWithId;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional(propagation = Propagation.MANDATORY)
@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final EventTypeMapper eventTypeMapper;

    @SneakyThrows
    public <T extends Event> EventWithId<T> appendEvent(@NonNull Event event) {
        List<EventWithId<T>> result = jdbcTemplate.query("""
                        INSERT INTO ES_EVENT (TRANSACTION_ID, AGGREGATE_ID, VERSION, EVENT_TYPE, JSON_DATA)
                        VALUES(pg_current_xact_id(), :aggregateId, :version, :eventType, :jsonObj::json)
                        RETURNING ID, TRANSACTION_ID::text, EVENT_TYPE, JSON_DATA
                        """,
                Map.of(
                        "aggregateId", event.getAggregateId(),
                        "version", event.getVersion(),
                        "eventType", event.getEventType(),
                        "jsonObj", objectMapper.writeValueAsString(event)
                ),
                this::toEvent);
        return result.get(0);
    }

    public List<EventWithId<Event>> readEvents(@NonNull UUID aggregateId,
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

    public List<EventWithId<Event>> readEventsAfterCheckpoint(@NonNull String aggregateType,
                                                              @NonNull BigInteger lastProcessedTransactionId,
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
                        "aggregateType", aggregateType,
                        "lastProcessedTransactionId", lastProcessedTransactionId.toString(),
                        "lastProcessedEventId", lastProcessedEventId
                ),
                this::toEvent);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T extends Event> EventWithId<T> toEvent(ResultSet rs, int rowNum) {
        long id = rs.getLong("ID");
        String transactionId = rs.getString("TRANSACTION_ID");
        String eventType = rs.getString("EVENT_TYPE");
        PGobject jsonObj = (PGobject) rs.getObject("JSON_DATA");
        String json = jsonObj.getValue();
        Class<? extends Event> eventClass = eventTypeMapper.getClassByEventType(eventType);
        Event event = objectMapper.readValue(json, eventClass);
        return new EventWithId<>(id, new BigInteger(transactionId), (T) event);
    }
}
