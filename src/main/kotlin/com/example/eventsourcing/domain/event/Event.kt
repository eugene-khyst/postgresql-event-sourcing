package com.example.eventsourcing.domain.event

import java.time.Instant
import java.util.*

open class Event protected constructor(val aggregateId: UUID, val version: Int) {
    val createdDate = Instant.now()
    val eventType: EventType = EventType.Companion.fromClass(this.javaClass)

    override fun toString(): String {
        return "Event(aggregateId=" + aggregateId + ", version=" + version + ", createdDate=" + createdDate + ", eventType=" + eventType + ")"
    }
}
