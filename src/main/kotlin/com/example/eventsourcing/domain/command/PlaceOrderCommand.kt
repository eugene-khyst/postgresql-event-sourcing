package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.dto.WaypointDto
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class PlaceOrderCommand(
    val riderId: UUID,
    val price: BigDecimal,
    val route: List<WaypointDto>,
    override val createdAt: Instant
) : Command {
    override val aggregateType: AggregateType = AggregateType.ORDER

    override val aggregateId: UUID = UUID.randomUUID()
}
