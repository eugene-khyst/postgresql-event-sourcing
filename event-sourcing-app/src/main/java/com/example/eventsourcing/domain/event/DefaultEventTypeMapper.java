package com.example.eventsourcing.domain.event;

import eventsourcing.postgresql.domain.event.Event;
import eventsourcing.postgresql.domain.event.EventTypeMapper;
import org.springframework.stereotype.Component;

@Component
public class DefaultEventTypeMapper implements EventTypeMapper {

    @Override
    public Class<? extends Event> getClassByEventType(String eventType) {
        return EventType.valueOf(eventType).getEventClass();
    }
}
