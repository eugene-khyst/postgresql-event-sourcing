package com.example.eventsourcing.config;

import com.example.eventsourcing.domain.AggregateType;

import java.util.Map;

public class EventSourcingProperties {

    private static final SnapshottingProperties NO_SNAPSHOTTING = new SnapshottingProperties(false, 0);

    private final Map<AggregateType, SnapshottingProperties> snapshotting;

    public EventSourcingProperties(Map<AggregateType, SnapshottingProperties> snapshotting) {
        this.snapshotting = snapshotting;
    }

    public SnapshottingProperties getSnapshotting(AggregateType aggregateType) {
        return snapshotting.getOrDefault(aggregateType, NO_SNAPSHOTTING);
    }

    public record SnapshottingProperties(
            boolean enabled,
            int nthEvent
    ) {
    }
}
