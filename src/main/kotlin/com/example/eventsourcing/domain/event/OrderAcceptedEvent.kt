package com.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public final class OrderAcceptedEvent extends Event {

    private final UUID driverId;

    @JsonCreator
    public OrderAcceptedEvent(UUID aggregateId, int version, UUID driverId) {
        super(aggregateId, version);
        this.driverId = driverId;
    }

    public static OrderAcceptedEventBuilder builder() {
        return new OrderAcceptedEventBuilder();
    }

    public UUID getDriverId() {
        return this.driverId;
    }

    public String toString() {
        return "OrderAcceptedEvent(super=" + super.toString() + ", driverId=" + this.getDriverId() + ")";
    }

    public static class OrderAcceptedEventBuilder {
        private UUID aggregateId;
        private int version;
        private UUID driverId;

        OrderAcceptedEventBuilder() {
        }

        public OrderAcceptedEventBuilder aggregateId(UUID aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public OrderAcceptedEventBuilder version(int version) {
            this.version = version;
            return this;
        }

        public OrderAcceptedEventBuilder driverId(UUID driverId) {
            this.driverId = driverId;
            return this;
        }

        public OrderAcceptedEvent build() {
            return new OrderAcceptedEvent(this.aggregateId, this.version, this.driverId);
        }

        public String toString() {
            return "OrderAcceptedEvent.OrderAcceptedEventBuilder(aggregateId=" + this.aggregateId + ", version=" + this.version + ", driverId=" + this.driverId + ")";
        }
    }
}
