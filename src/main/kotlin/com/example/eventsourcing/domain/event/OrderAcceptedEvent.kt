package com.example.eventsourcing.domain.event

import java.util.*

class OrderAcceptedEvent(aggregateId: UUID, version: Int, val driverId: UUID) :
    Event(aggregateId, version)
