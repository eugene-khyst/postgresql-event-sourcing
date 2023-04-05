package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
public final class CompleteOrderCommand extends Command {

    public CompleteOrderCommand(UUID aggregateId) {
        super(AggregateType.ORDER, aggregateId);
    }
}