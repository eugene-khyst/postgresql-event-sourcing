package com.example.eventsourcing.domain.event

import java.math.BigDecimal
import java.util.*

class OrderPriceAdjustedEvent(
    aggregateId: UUID,
    version: Int,
    val newPrice: BigDecimal
) : Event(aggregateId, version) {

    override fun toString(): String {
        return "OrderPriceAdjustedEvent(super=" + super.toString() + ", newPrice=" + newPrice + ")"
    }
}
