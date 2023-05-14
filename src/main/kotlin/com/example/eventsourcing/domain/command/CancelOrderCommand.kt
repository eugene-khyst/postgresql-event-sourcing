package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

class CancelOrderCommand(override val aggregateId: UUID) : Command {
    override val aggregateType: AggregateType = AggregateType.ORDER
}
