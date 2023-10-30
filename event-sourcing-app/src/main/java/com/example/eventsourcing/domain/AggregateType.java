package com.example.eventsourcing.domain;

import eventsourcing.postgresql.domain.Aggregate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum AggregateType {

    ORDER(OrderAggregate.class);

    private final Class<? extends Aggregate> aggregateClass;
}
