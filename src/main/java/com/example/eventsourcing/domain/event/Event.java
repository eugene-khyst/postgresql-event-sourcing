package com.example.eventsourcing.domain.event;

import java.time.Instant;
import java.util.UUID;

public class Event {

    protected final UUID aggregateId;
    protected final int version;
    protected final Instant createdDate = Instant.now();
    protected final EventType eventType = EventType.fromClass(this.getClass());

    protected Event(UUID aggregateId, int version) {
        this.aggregateId = aggregateId;
        this.version = version;
    }

    public UUID getAggregateId() {
        return this.aggregateId;
    }

    public int getVersion() {
        return this.version;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String toString() {
        return "Event(aggregateId=" + this.getAggregateId() + ", version=" + this.getVersion() + ", createdDate=" + this.getCreatedDate() + ", eventType=" + this.getEventType() + ")";
    }
}
