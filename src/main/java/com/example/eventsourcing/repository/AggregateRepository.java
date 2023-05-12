package com.example.eventsourcing.repository;

import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.AggregateType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Transactional(propagation = Propagation.MANDATORY)
@Repository
public class AggregateRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AggregateRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void createAggregateIfAbsent(AggregateType aggregateType,
                                        UUID aggregateId) {
        jdbcTemplate.update("""
                        INSERT INTO ES_AGGREGATE (ID, VERSION, AGGREGATE_TYPE)
                        VALUES (:aggregateId, 0, :aggregateType)
                        ON CONFLICT DO NOTHING
                        """,
                Map.of(
                        "aggregateId", aggregateId,
                        "aggregateType", aggregateType.toString()
                ));
    }

    public boolean checkAndUpdateAggregateVersion(UUID aggregateId,
                                                  int expectedVersion,
                                                  int newVersion) {
        int updatedRows = jdbcTemplate.update("""
                        UPDATE ES_AGGREGATE
                           SET VERSION = :newVersion
                         WHERE ID = :aggregateId
                           AND VERSION = :expectedVersion
                        """,
                Map.of(
                        "newVersion", newVersion,
                        "aggregateId", aggregateId,
                        "expectedVersion", expectedVersion
                ));
        return updatedRows > 0;
    }

    public void createAggregateSnapshot(Aggregate aggregate) {
        try {
            jdbcTemplate.update("""
                            INSERT INTO ES_AGGREGATE_SNAPSHOT (AGGREGATE_ID, VERSION, JSON_DATA)
                            VALUES (:aggregateId, :version, :jsonObj::json)
                            """,
                    Map.of(
                            "aggregateId", aggregate.getAggregateId(),
                            "version", aggregate.getVersion(),
                            "jsonObj", objectMapper.writeValueAsString(aggregate)
                    ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Aggregate> readAggregateSnapshot(UUID aggregateId,
                                                     @Nullable Integer version) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregateId);
        parameters.addValue("version", version, Types.INTEGER);

        return jdbcTemplate.query("""
                        SELECT a.AGGREGATE_TYPE,
                               s.JSON_DATA
                          FROM ES_AGGREGATE_SNAPSHOT s
                          JOIN ES_AGGREGATE a ON a.ID = s.AGGREGATE_ID
                         WHERE s.AGGREGATE_ID = :aggregateId
                           AND (:version IS NULL OR s.VERSION <= :version)
                         ORDER BY s.VERSION DESC
                         LIMIT 1
                        """,
                parameters,
                this::toAggregate
        ).stream().findFirst();
    }

    private Aggregate toAggregate(ResultSet rs, int rowNum) throws SQLException {
        try {
            AggregateType aggregateType = AggregateType.valueOf(rs.getString("AGGREGATE_TYPE"));
            PGobject jsonObj = (PGobject) rs.getObject("JSON_DATA");
            String json = jsonObj.getValue();
            return objectMapper.readValue(json, aggregateType.getAggregateClass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
