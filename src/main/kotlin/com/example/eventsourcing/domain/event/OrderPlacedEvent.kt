package com.example.eventsourcing.domain.event

import com.example.eventsourcing.dto.WaypointDto
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class OrderPlacedEvent(
    aggregateId: UUID,
    version: Int,
    createdAt: Instant,
    val riderId: UUID,
    val price: BigDecimal,
    val route: List<WaypointDto>
) : Event(aggregateId, version, createdAt)
