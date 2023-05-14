package com.example.eventsourcing.config

data class SnapshottingProperties(val enabled: Boolean, val nthEvent: Int) {
    companion object {
        val NO_SNAPSHOTTING = SnapshottingProperties(false, 0)
    }
}
