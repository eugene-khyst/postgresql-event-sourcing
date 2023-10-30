package com.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import eventsourcing.postgresql.domain.event.Event;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class OrderPriceAdjustedEvent extends Event {

    private final BigDecimal newPrice;

    @JsonCreator
    @Builder
    public OrderPriceAdjustedEvent(UUID aggregateId,
                                   int version,
                                   BigDecimal newPrice) {
        super(aggregateId, version);
        this.newPrice = newPrice;
    }

    @Nonnull
    @Override
    public String getEventType() {
        return EventType.ORDER_PRICE_ADJUSTED.toString();
    }
}
