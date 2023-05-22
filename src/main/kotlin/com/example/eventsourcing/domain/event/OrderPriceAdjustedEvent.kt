package com.example.eventsourcing.domain.event

import java.math.BigDecimal
import java.time.Instant
import java.util.*

class OrderPriceAdjustedEvent(
    aggregateId: UUID,
    version: Int,
    createdAt: Instant,
    val newPrice: BigDecimal
) : Event(aggregateId, version, createdAt)
