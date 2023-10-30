package com.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import eventsourcing.postgresql.domain.event.Event;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
public final class OrderCompletedEvent extends Event {

    @JsonCreator
    @Builder
    public OrderCompletedEvent(UUID aggregateId, int version) {
        super(aggregateId, version);
    }

    @Nonnull
    @Override
    public String getEventType() {
        return EventType.ORDER_COMPLETED.toString();
    }
}
