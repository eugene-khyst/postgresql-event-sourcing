package com.example.eventsourcing.service.event

import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.domain.event.EventWithId

interface AsyncEventHandler {
    fun handleEvent(event: EventWithId<Event>)
    val aggregateType: AggregateType?
    val subscriptionName: String?
        get() = javaClass.name
}
