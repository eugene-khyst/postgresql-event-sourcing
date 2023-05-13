package com.example.eventsourcing.domain.event

import java.util.*

class OrderCompletedEvent(aggregateId: UUID, version: Int) : Event(aggregateId, version)
