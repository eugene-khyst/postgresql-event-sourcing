package com.example.eventsourcing.config

import com.example.eventsourcing.domain.AggregateType

class EventSourcingProperties(private val snapshotting: Map<AggregateType, SnapshottingProperties>) {
    fun getSnapshotting(aggregateType: AggregateType): SnapshottingProperties {
        return snapshotting[aggregateType] ?: NO_SNAPSHOTTING
    }

    @JvmRecord
    data class SnapshottingProperties(val enabled: Boolean, val nthEvent: Int)
    companion object {
        private val NO_SNAPSHOTTING = SnapshottingProperties(false, 0)
    }
}
