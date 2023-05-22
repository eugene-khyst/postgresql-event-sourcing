package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.time.Instant
import java.util.*

class CancelOrderCommand(
    override val aggregateId: UUID,
    override val createdAt: Instant
) : Command {
    override val aggregateType: AggregateType = AggregateType.ORDER
}
