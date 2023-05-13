package com.example.eventsourcing.service.event

import com.example.eventsourcing.domain.Aggregate
import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.domain.event.EventWithId

interface SyncEventHandler {
    fun handleEvents(
        events: List<EventWithId<Event>>,
        aggregate: Aggregate
    )

    val aggregateType: AggregateType
}
