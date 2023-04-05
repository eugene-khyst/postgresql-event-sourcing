package com.example.eventsourcing.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum EventType {

    ORDER_PLACED(OrderPlacedEvent.class),
    ORDER_PRICE_ADJUSTED(OrderPriceAdjustedEvent.class),
    ORDER_ACCEPTED(OrderAcceptedEvent.class),
    ORDER_COMPLETED(OrderCompletedEvent.class),
    ORDER_CANCELLED(OrderCancelledEvent.class);

    @Getter
    private final Class<? extends Event> eventClass;

    public static EventType fromClass(Class<? extends Event> eventClass) {
        return Arrays.stream(EventType.values())
                .filter(eventType -> eventType.eventClass == eventClass)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown event class %s".formatted(eventClass)));
    }
}
