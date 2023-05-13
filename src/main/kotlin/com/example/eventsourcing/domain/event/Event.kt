package com.example.eventsourcing.domain.event

import java.time.Instant
import java.util.*

open class Event protected constructor(val aggregateId: UUID, val version: Int) {
    val createdDate: Instant = Instant.now()
    val eventType: EventType = EventType.fromClass(this.javaClass)
}
