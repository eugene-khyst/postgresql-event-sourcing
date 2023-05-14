package com.example.eventsourcing.service.event

import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.domain.event.EventWithId

interface AsyncEventHandler {
    fun handleEvent(event: EventWithId<Event>)
    fun subscriptionName(): String = javaClass.name

    val aggregateType: AggregateType
}
