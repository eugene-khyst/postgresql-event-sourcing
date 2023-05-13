package com.example.eventsourcing.domain.event

import java.util.*

enum class EventType(val eventClass: Class<out Event>) {
    ORDER_PLACED(OrderPlacedEvent::class.java),
    ORDER_PRICE_ADJUSTED(OrderPriceAdjustedEvent::class.java),
    ORDER_ACCEPTED(OrderAcceptedEvent::class.java),
    ORDER_COMPLETED(OrderCompletedEvent::class.java),
    ORDER_CANCELLED(OrderCancelledEvent::class.java);

    companion object {
        fun fromClass(eventClass: Class<out Event?>): EventType {
            return Arrays.stream(values())
                .filter { eventType: EventType -> eventType.eventClass == eventClass }
                .findFirst()
                .orElseThrow { RuntimeException("Unknown event class %s".formatted(eventClass)) }
        }
    }
}
