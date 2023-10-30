package com.example.eventsourcing.domain;

import eventsourcing.postgresql.domain.Aggregate;
import eventsourcing.postgresql.domain.AggregateTypeMapper;
import org.springframework.stereotype.Component;

@Component
public class DefaultAggregateTypeMapper implements AggregateTypeMapper {

    @Override
    public Class<? extends Aggregate> getClassByAggregateType(String aggregateType) {
        return AggregateType.valueOf(aggregateType).getAggregateClass();
    }
}
