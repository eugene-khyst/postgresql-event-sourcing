package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;
import eventsourcing.postgresql.domain.command.Command;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class AcceptOrderCommand extends Command {

    private final UUID driverId;

    public AcceptOrderCommand(UUID aggregateId,
                              UUID driverId) {
        super(AggregateType.ORDER.toString(), aggregateId);
        this.driverId = driverId;
    }
}
