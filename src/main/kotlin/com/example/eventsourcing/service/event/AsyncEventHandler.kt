package com.example.eventsourcing.service.event;

import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventWithId;

public interface AsyncEventHandler {

    void handleEvent(EventWithId<Event> event);

    AggregateType getAggregateType();

    default String getSubscriptionName() {
        return getClass().getName();
    }
}
