package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

class AcceptOrderCommand(
    aggregateId: UUID,
    val driverId: UUID
) : Command(AggregateType.ORDER, aggregateId)
