package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.dto.WaypointDto
import java.math.BigDecimal
import java.util.*

class PlaceOrderCommand(
    val riderId: UUID,
    val price: BigDecimal,
    val route: List<WaypointDto>
) : Command(AggregateType.ORDER, generateAggregateId()) {

    override fun toString(): String {
        return "PlaceOrderCommand(super=" + super.toString() + ", riderId=" + riderId + ", price=" + price + ", route=" + route + ")"
    }

    companion object {
        private fun generateAggregateId(): UUID {
            return UUID.randomUUID()
        }
    }
}
