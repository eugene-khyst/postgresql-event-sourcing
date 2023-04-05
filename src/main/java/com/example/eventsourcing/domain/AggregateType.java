package com.example.eventsourcing.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AggregateType {

    ORDER(OrderAggregate.class);

    @Getter
    private final Class<? extends Aggregate> aggregateClass;

    @SneakyThrows(ReflectiveOperationException.class)
    @SuppressWarnings("unchecked")
    public <T extends Aggregate> T newInstance(UUID aggregateId) {
        var constructor = aggregateClass.getDeclaredConstructor(UUID.class, Integer.TYPE);
        return (T) constructor.newInstance(aggregateId, 0);
    }
}
