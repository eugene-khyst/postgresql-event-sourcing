package com.example.eventsourcing.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Event {

    protected final UUID aggregateId;
    protected final int version;
    protected final Instant createdDate = Instant.now();
    protected final EventType eventType = EventType.fromClass(this.getClass());
}
