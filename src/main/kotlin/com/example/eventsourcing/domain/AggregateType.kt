package com.example.eventsourcing.domain;

import java.lang.reflect.Constructor;
import java.util.UUID;

public enum AggregateType {

    ORDER(OrderAggregate.class);

    private final Class<? extends Aggregate> aggregateClass;

    AggregateType(Class<? extends Aggregate> aggregateClass) {
        this.aggregateClass = aggregateClass;
    }

    public <T extends Aggregate> T newInstance(UUID aggregateId) {
        try {
            Constructor<? extends Aggregate> constructor = aggregateClass.getDeclaredConstructor(UUID.class, Integer.TYPE);
            return (T) constructor.newInstance(aggregateId, 0);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<? extends Aggregate> getAggregateClass() {
        return this.aggregateClass;
    }
}
