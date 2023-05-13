package com.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public final class OrderCompletedEvent extends Event {

    @JsonCreator
    public OrderCompletedEvent(UUID aggregateId, int version) {
        super(aggregateId, version);
    }

    public static OrderCompletedEventBuilder builder() {
        return new OrderCompletedEventBuilder();
    }

    public String toString() {
        return "OrderCompletedEvent(super=" + super.toString() + ")";
    }

    public static class OrderCompletedEventBuilder {
        private UUID aggregateId;
        private int version;

        OrderCompletedEventBuilder() {
        }

        public OrderCompletedEventBuilder aggregateId(UUID aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public OrderCompletedEventBuilder version(int version) {
            this.version = version;
            return this;
        }

        public OrderCompletedEvent build() {
            return new OrderCompletedEvent(this.aggregateId, this.version);
        }

        public String toString() {
            return "OrderCompletedEvent.OrderCompletedEventBuilder(aggregateId=" + this.aggregateId + ", version=" + this.version + ")";
        }
    }
}
