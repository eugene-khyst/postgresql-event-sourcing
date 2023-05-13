package com.example.eventsourcing.domain.event;

import com.example.eventsourcing.dto.WaypointDto;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class OrderPlacedEvent extends Event {

    private final UUID riderId;
    private final BigDecimal price;
    private final List<WaypointDto> route;

    @JsonCreator
    public OrderPlacedEvent(UUID aggregateId,
                            int version,
                            UUID riderId,
                            BigDecimal price,
                            List<WaypointDto> route) {
        super(aggregateId, version);
        this.riderId = riderId;
        this.price = price;
        this.route = List.copyOf(route);
    }

    public static OrderPlacedEventBuilder builder() {
        return new OrderPlacedEventBuilder();
    }

    public UUID getRiderId() {
        return this.riderId;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public List<WaypointDto> getRoute() {
        return this.route;
    }

    public String toString() {
        return "OrderPlacedEvent(super=" + super.toString() + ", riderId=" + this.getRiderId() + ", price=" + this.getPrice() + ", route=" + this.getRoute() + ")";
    }

    public static class OrderPlacedEventBuilder {
        private UUID aggregateId;
        private int version;
        private UUID riderId;
        private BigDecimal price;
        private List<WaypointDto> route;

        OrderPlacedEventBuilder() {
        }

        public OrderPlacedEventBuilder aggregateId(UUID aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public OrderPlacedEventBuilder version(int version) {
            this.version = version;
            return this;
        }

        public OrderPlacedEventBuilder riderId(UUID riderId) {
            this.riderId = riderId;
            return this;
        }

        public OrderPlacedEventBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public OrderPlacedEventBuilder route(List<WaypointDto> route) {
            this.route = route;
            return this;
        }

        public OrderPlacedEvent build() {
            return new OrderPlacedEvent(this.aggregateId, this.version, this.riderId, this.price, this.route);
        }

        public String toString() {
            return "OrderPlacedEvent.OrderPlacedEventBuilder(aggregateId=" + this.aggregateId + ", version=" + this.version + ", riderId=" + this.riderId + ", price=" + this.price + ", route=" + this.route + ")";
        }
    }
}
