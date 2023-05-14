package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

class AcceptOrderCommand(
    override val aggregateId: UUID,
    val driverId: UUID
) : Command {
    override val aggregateType: AggregateType = AggregateType.ORDER
}
