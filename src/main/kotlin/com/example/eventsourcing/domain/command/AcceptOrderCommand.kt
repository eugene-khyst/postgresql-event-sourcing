package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

class AcceptOrderCommand(
    aggregateId: UUID,
    val driverId: UUID
) : Command(AggregateType.ORDER, aggregateId) {

    override fun toString(): String {
        return "AcceptOrderCommand(super=" + super.toString() + ", driverId=" + driverId + ")"
    }
}
