package com.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;
import java.util.UUID;

public final class OrderPriceAdjustedEvent extends Event {

    private final BigDecimal newPrice;

    @JsonCreator
    public OrderPriceAdjustedEvent(UUID aggregateId,
                                   int version,
                                   BigDecimal newPrice) {
        super(aggregateId, version);
        this.newPrice = newPrice;
    }

    public static OrderPriceAdjustedEventBuilder builder() {
        return new OrderPriceAdjustedEventBuilder();
    }

    public BigDecimal getNewPrice() {
        return this.newPrice;
    }

    public String toString() {
        return "OrderPriceAdjustedEvent(super=" + super.toString() + ", newPrice=" + this.getNewPrice() + ")";
    }

    public static class OrderPriceAdjustedEventBuilder {
        private UUID aggregateId;
        private int version;
        private BigDecimal newPrice;

        OrderPriceAdjustedEventBuilder() {
        }

        public OrderPriceAdjustedEventBuilder aggregateId(UUID aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public OrderPriceAdjustedEventBuilder version(int version) {
            this.version = version;
            return this;
        }

        public OrderPriceAdjustedEventBuilder newPrice(BigDecimal newPrice) {
            this.newPrice = newPrice;
            return this;
        }

        public OrderPriceAdjustedEvent build() {
            return new OrderPriceAdjustedEvent(this.aggregateId, this.version, this.newPrice);
        }

        public String toString() {
            return "OrderPriceAdjustedEvent.OrderPriceAdjustedEventBuilder(aggregateId=" + this.aggregateId + ", version=" + this.version + ", newPrice=" + this.newPrice + ")";
        }
    }
}
