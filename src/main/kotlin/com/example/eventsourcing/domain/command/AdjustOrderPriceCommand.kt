package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class AdjustOrderPriceCommand(
    override val aggregateId: UUID,
    override val createdAt: Instant,
    val newPrice: BigDecimal
) : Command {
    override val aggregateType: AggregateType = AggregateType.ORDER
}
