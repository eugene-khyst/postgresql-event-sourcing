package com.example.eventsourcing.domain.event;

import java.util.Arrays;

public enum EventType {

    ORDER_PLACED(OrderPlacedEvent.class),
    ORDER_PRICE_ADJUSTED(OrderPriceAdjustedEvent.class),
    ORDER_ACCEPTED(OrderAcceptedEvent.class),
    ORDER_COMPLETED(OrderCompletedEvent.class),
    ORDER_CANCELLED(OrderCancelledEvent.class);

    private final Class<? extends Event> eventClass;

    EventType(Class<? extends Event> eventClass) {
        this.eventClass = eventClass;
    }

    public static EventType fromClass(Class<? extends Event> eventClass) {
        return Arrays.stream(EventType.values())
                .filter(eventType -> eventType.eventClass == eventClass)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown event class %s".formatted(eventClass)));
    }

    public Class<? extends Event> getEventClass() {
        return this.eventClass;
    }
}
