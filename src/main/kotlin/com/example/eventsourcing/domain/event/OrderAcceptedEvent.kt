package com.example.eventsourcing.domain.event

import java.util.*

class OrderAcceptedEvent(aggregateId: UUID, version: Int, val driverId: UUID) :
    Event(aggregateId, version) {

    override fun toString(): String {
        return "OrderAcceptedEvent(super=" + super.toString() + ", driverId=" + driverId + ")"
    }
}
