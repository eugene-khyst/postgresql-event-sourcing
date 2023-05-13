package com.example.eventsourcing.domain.event

import com.example.eventsourcing.dto.WaypointDto
import java.math.BigDecimal
import java.util.*

class OrderPlacedEvent(
    aggregateId: UUID,
    version: Int,
    val riderId: UUID,
    val price: BigDecimal,
    val route: List<WaypointDto>
) : Event(aggregateId, version) {

    override fun toString(): String {
        return "OrderPlacedEvent(super=" + super.toString() + ", riderId=" + riderId + ", price=" + price + ", route=" + route + ")"
    }
}
