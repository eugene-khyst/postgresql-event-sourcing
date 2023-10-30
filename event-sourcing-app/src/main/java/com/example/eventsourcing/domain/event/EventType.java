package com.example.eventsourcing.domain.event;

import eventsourcing.postgresql.domain.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum EventType {

    ORDER_PLACED(OrderPlacedEvent.class),
    ORDER_PRICE_ADJUSTED(OrderPriceAdjustedEvent.class),
    ORDER_ACCEPTED(OrderAcceptedEvent.class),
    ORDER_COMPLETED(OrderCompletedEvent.class),
    ORDER_CANCELLED(OrderCancelledEvent.class);

    private final Class<? extends Event> eventClass;
}
