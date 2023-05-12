package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;

import java.util.UUID;

public final class CancelOrderCommand extends Command {

    public CancelOrderCommand(UUID aggregateId) {
        super(AggregateType.ORDER, aggregateId);
    }

    public String toString() {
        return "CancelOrderCommand(super=" + super.toString() + ")";
    }
}
