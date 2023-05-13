package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

open class Command protected constructor(val aggregateType: AggregateType, val aggregateId: UUID) {

    override fun toString(): String {
        return "Command(aggregateType=" + aggregateType + ", aggregateId=" + aggregateId + ")"
    }
}
