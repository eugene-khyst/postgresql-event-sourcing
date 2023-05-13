package com.example.eventsourcing.domain.command

import com.example.eventsourcing.domain.AggregateType
import java.util.*

open class Command protected constructor(val aggregateType: AggregateType, val aggregateId: UUID)
