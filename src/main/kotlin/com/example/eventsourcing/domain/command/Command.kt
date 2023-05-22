package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.time.Instant
import java.util.*

interface Command {
    val aggregateType: AggregateType
    val aggregateId: UUID
    val createdAt: Instant
}
