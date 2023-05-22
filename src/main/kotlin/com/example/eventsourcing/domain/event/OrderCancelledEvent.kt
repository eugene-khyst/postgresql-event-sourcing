package com.example.eventsourcing.domain.event

import java.time.Instant
import java.util.*

class OrderCancelledEvent(
    aggregateId: UUID,
    version: Int,
    createdAt: Instant
) : Event(aggregateId, version, createdAt)
