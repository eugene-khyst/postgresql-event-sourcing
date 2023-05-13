package com.example.eventsourcing.service.event;

import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventWithId;

import java.util.List;

public interface SyncEventHandler {

    void handleEvents(List<EventWithId<Event>> events,
                      Aggregate aggregate);

    AggregateType getAggregateType();
}
