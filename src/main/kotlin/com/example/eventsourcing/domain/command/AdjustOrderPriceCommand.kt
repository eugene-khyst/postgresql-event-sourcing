package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.math.BigDecimal
import java.util.*

class AdjustOrderPriceCommand(
    aggregateId: UUID,
    val newPrice: BigDecimal
) : Command(AggregateType.ORDER, aggregateId)
