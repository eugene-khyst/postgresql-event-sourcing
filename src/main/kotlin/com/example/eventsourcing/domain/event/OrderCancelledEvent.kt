package com.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public final class OrderCancelledEvent extends Event {

    @JsonCreator
    public OrderCancelledEvent(UUID aggregateId, int version) {
        super(aggregateId, version);
    }

    public static OrderCancelledEventBuilder builder() {
        return new OrderCancelledEventBuilder();
    }

    public String toString() {
        return "OrderCancelledEvent(super=" + super.toString() + ")";
    }

    public static class OrderCancelledEventBuilder {
        private UUID aggregateId;
        private int version;

        OrderCancelledEventBuilder() {
        }

        public OrderCancelledEventBuilder aggregateId(UUID aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public OrderCancelledEventBuilder version(int version) {
            this.version = version;
            return this;
        }

        public OrderCancelledEvent build() {
            return new OrderCancelledEvent(this.aggregateId, this.version);
        }

        public String toString() {
            return "OrderCancelledEvent.OrderCancelledEventBuilder(aggregateId=" + this.aggregateId + ", version=" + this.version + ")";
        }
    }
}
