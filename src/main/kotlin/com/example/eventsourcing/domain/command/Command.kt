package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

interface Command {
    val aggregateType: AggregateType
    val aggregateId: UUID
}
