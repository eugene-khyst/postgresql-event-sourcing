package com.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import eventsourcing.postgresql.domain.event.Event;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class OrderAcceptedEvent extends Event {

    private final UUID driverId;

    @JsonCreator
    @Builder
    public OrderAcceptedEvent(UUID aggregateId, int version, UUID driverId) {
        super(aggregateId, version);
        this.driverId = driverId;
    }

    @Nonnull
    @Override
    public String getEventType() {
        return EventType.ORDER_ACCEPTED.toString();
    }
}
