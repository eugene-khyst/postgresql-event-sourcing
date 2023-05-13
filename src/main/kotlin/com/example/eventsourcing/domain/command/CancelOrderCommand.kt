package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

class CancelOrderCommand(aggregateId: UUID) : Command(AggregateType.ORDER, aggregateId) {
    override fun toString(): String {
        return "CancelOrderCommand(super=" + super.toString() + ")"
    }
}
