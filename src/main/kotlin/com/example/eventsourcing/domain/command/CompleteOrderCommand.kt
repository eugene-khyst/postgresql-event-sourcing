package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

class CompleteOrderCommand(aggregateId: UUID) : Command(AggregateType.ORDER, aggregateId) {
    override fun toString(): String {
        return "CompleteOrderCommand(super=" + super.toString() + ")"
    }
}
