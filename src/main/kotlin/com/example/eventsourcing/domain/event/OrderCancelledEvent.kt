package com.example.eventsourcing.domain.event

import java.util.*

class OrderCancelledEvent(aggregateId: UUID, version: Int) : Event(aggregateId, version)
