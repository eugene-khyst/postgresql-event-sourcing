package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;

import java.util.UUID;

public final class AcceptOrderCommand extends Command {

    private final UUID driverId;

    public AcceptOrderCommand(UUID aggregateId,
                              UUID driverId) {
        super(AggregateType.ORDER, aggregateId);
        this.driverId = driverId;
    }

    public UUID getDriverId() {
        return this.driverId;
    }

    public String toString() {
        return "AcceptOrderCommand(super=" + super.toString() + ", driverId=" + this.getDriverId() + ")";
    }
}
