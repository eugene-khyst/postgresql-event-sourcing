package com.example.eventsourcing.config

import com.example.eventsourcing.config.SnapshottingProperties.Companion.NO_SNAPSHOTTING
import com.example.eventsourcing.domain.AggregateType

class EventSourcingProperties(private val snapshotting: Map<AggregateType, SnapshottingProperties>) {
    fun getSnapshotting(aggregateType: AggregateType): SnapshottingProperties {
        return snapshotting[aggregateType] ?: NO_SNAPSHOTTING
    }
}
