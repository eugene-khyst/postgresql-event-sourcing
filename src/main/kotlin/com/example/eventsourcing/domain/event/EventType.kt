package com.example.eventsourcing.domain.event

enum class EventType(val eventClass: Class<out Event>) {
    ORDER_PLACED(OrderPlacedEvent::class.java),
    ORDER_PRICE_ADJUSTED(OrderPriceAdjustedEvent::class.java),
    ORDER_ACCEPTED(OrderAcceptedEvent::class.java),
    ORDER_COMPLETED(OrderCompletedEvent::class.java),
    ORDER_CANCELLED(OrderCancelledEvent::class.java);

    companion object {
        fun fromClass(eventClass: Class<out Event?>): EventType {
            return values().firstOrNull { eventType: EventType -> eventType.eventClass == eventClass }
                ?: throw RuntimeException("Unknown event class %s".format(eventClass))
        }
    }
}
