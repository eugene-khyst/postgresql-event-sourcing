package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;

import java.util.UUID;

public class Command {

    protected final AggregateType aggregateType;
    protected final UUID aggregateId;

    protected Command(AggregateType aggregateType, UUID aggregateId) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }

    public AggregateType getAggregateType() {
        return this.aggregateType;
    }

    public UUID getAggregateId() {
        return this.aggregateId;
    }

    public String toString() {
        return "Command(aggregateType=" + this.getAggregateType() + ", aggregateId=" + this.getAggregateId() + ")";
    }
}
