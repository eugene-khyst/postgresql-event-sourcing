package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Command {

    protected final AggregateType aggregateType;
    protected final UUID aggregateId;
}
