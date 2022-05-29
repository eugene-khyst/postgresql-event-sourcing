package com.example.eventsourcing.eventstore.repository;

import com.example.eventsourcing.eventstore.eventsourcing.Event;
import com.example.eventsourcing.eventstore.service.EventJsonSerde;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class OrderEventRepository {

  private final JdbcTemplate jdbcTemplate;
  private final EventJsonSerde jsonSerde;

  public void createAggregateIfNotExists(UUID aggregateId) {
    jdbcTemplate.update("""
            INSERT INTO ORDER_AGGREGATE (ID, VERSION)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """,
        aggregateId,
        0);
  }

  public boolean checkAndIncrementAggregateVersion(UUID aggregateId, int expectedVersion) {
    return jdbcTemplate.update("""
            UPDATE ORDER_AGGREGATE
               SET VERSION = VERSION + 1
             WHERE ID = ?
               AND VERSION = ?
            """,
            aggregateId,
            expectedVersion)
        > 0;
  }

  public boolean append(Event event) {
    return jdbcTemplate.update("""
            INSERT INTO ORDER_EVENT(AGGREGATE_ID, VERSION, EVENT_TYPE, JSON_DATA)
            VALUES(?, ?, ?, ?)
            """,
            ps -> {
              ps.setObject(1, event.getAggregateId());
              ps.setInt(2, event.getVersion());
              ps.setString(3, event.getEventType());

              PGobject jsonbObj = new PGobject();
              jsonbObj.setType("json");
              jsonbObj.setValue(jsonSerde.serialize(event));
              ps.setObject(4, jsonbObj);
            })
        > 0;
  }

  public List<Event> readEvents(UUID aggregateId) {
    return jdbcTemplate.query("""
            SELECT ID,
                   EVENT_TYPE,
                   JSON_DATA
              FROM ORDER_EVENT
             WHERE AGGREGATE_ID = ?
             ORDER BY VERSION ASC
            """,
        (rs, rowNum) -> toEvent(rs),
        aggregateId);
  }

  public List<Event> readEventsAfterVersion(UUID aggregateId, long version) {
    return jdbcTemplate.query("""
            SELECT ID,
                   EVENT_TYPE,
                   JSON_DATA
              FROM ORDER_EVENT
             WHERE AGGREGATE_ID = ?
               AND VERSION > ?
             ORDER BY VERSION ASC
            """,
        (rs, rowNum) -> toEvent(rs),
        aggregateId, version);
  }

  public List<Event> readEventsAfterId(long id) {
    return jdbcTemplate.query("""
            SELECT ID,
                   EVENT_TYPE,
                   JSON_DATA
              FROM ORDER_EVENT
             WHERE ID > ?
             ORDER BY ID ASC
            """,
        (rs, rowNum) -> toEvent(rs),
        id);
  }

  public void createSubscription(String subscriptionGroup) {
    jdbcTemplate.update("""
            INSERT INTO ORDER_EVENT_OUTBOX (SUBSCRIPTION_GROUP, LAST_ID)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """,
        subscriptionGroup,
        0);
  }

  public long readCheckpointAndAcquireLock(String subscriptionGroup) {
    return Optional.ofNullable(
            jdbcTemplate.queryForObject("""
                    SELECT LAST_ID
                      FROM ORDER_EVENT_OUTBOX
                     WHERE SUBSCRIPTION_GROUP = ?
                       FOR UPDATE NOWAIT
                    """,
                Long.class,
                subscriptionGroup))
        .orElse(0L);
  }

  public void updateCheckpoint(String subscriptionGroup, long id) {
    jdbcTemplate.update("""
            UPDATE ORDER_EVENT_OUTBOX
               SET LAST_ID = ?
             WHERE SUBSCRIPTION_GROUP = ?
            """,
        id,
        subscriptionGroup);
  }

  private Event toEvent(ResultSet rs) throws SQLException {
    long id = rs.getLong("ID");
    String eventType = rs.getString("EVENT_TYPE");
    PGobject jsonObj = (PGobject) rs.getObject("JSON_DATA");
    String json = jsonObj.getValue();
    Event event = jsonSerde.deserialize(json, eventType);
    event.setId(id);
    return event;
  }
}
