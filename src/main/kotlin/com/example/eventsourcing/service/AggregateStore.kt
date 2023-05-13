package com.example.eventsourcing.service

import com.example.eventsourcing.config.EventSourcingProperties
import com.example.eventsourcing.config.EventSourcingProperties.SnapshottingProperties
import com.example.eventsourcing.domain.Aggregate
import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.domain.event.EventWithId
import com.example.eventsourcing.error.OptimisticConcurrencyControlError
import com.example.eventsourcing.repository.AggregateRepository
import com.example.eventsourcing.repository.EventRepository
import org.slf4j.LoggerFactory
import java.util.*

class AggregateStore(
    private val aggregateRepository: AggregateRepository,
    private val eventRepository: EventRepository,
    private val properties: EventSourcingProperties
) {
    fun saveAggregate(aggregate: Aggregate): List<EventWithId<Event>> {
        log.debug("Saving aggregate {}", aggregate)
        val aggregateType = aggregate.aggregateType
        val aggregateId = aggregate.aggregateId
        aggregateRepository.createAggregateIfAbsent(aggregateType, aggregateId)
        val expectedVersion = aggregate.baseVersion
        val newVersion = aggregate.version
        if (!aggregateRepository.checkAndUpdateAggregateVersion(aggregateId, expectedVersion, newVersion)) {
            log.warn(
                "Optimistic concurrency control error in aggregate {} {}: " +
                        "actual version doesn't match expected version {}",
                aggregateType, aggregateId, expectedVersion
            )
            throw OptimisticConcurrencyControlError(expectedVersion.toLong())
        }
        val snapshotting = properties.getSnapshotting(aggregateType)
        val changes = aggregate.changes
        val newEvents: MutableList<EventWithId<Event>> = ArrayList()
        for (event in changes) {
            log.info("Appending {} event: {}", aggregateType, event)
            val newEvent: EventWithId<Event> = eventRepository.appendEvent(event)
            newEvents.add(newEvent)
            createAggregateSnapshot(snapshotting, aggregate)
        }
        return newEvents
    }

    private fun createAggregateSnapshot(
        snapshotting: SnapshottingProperties,
        aggregate: Aggregate
    ) {
        if (snapshotting.enabled && snapshotting.nthEvent > 1 && aggregate.version % snapshotting.nthEvent == 0) {
            log.info(
                "Creating {} aggregate {} version {} snapshot",
                aggregate.aggregateType, aggregate.aggregateId, aggregate.version
            )
            aggregateRepository.createAggregateSnapshot(aggregate)
        }
    }

    fun readAggregate(
        aggregateType: AggregateType,
        aggregateId: UUID,
        version: Int? = null
    ): Aggregate {
        log.debug("Reading {} aggregate {}", aggregateType, aggregateId)
        val (enabled) = properties.getSnapshotting(aggregateType)
        val aggregate: Aggregate
        aggregate = if (enabled) {
            readAggregateFromSnapshot(aggregateId, version)
                ?: readAggregateFromEvents(aggregateType, aggregateId, version).also {
                    log.debug("Aggregate {} snapshot not found", aggregateId)
                }
        } else {
            readAggregateFromEvents(aggregateType, aggregateId, version)
        }
        log.debug("Read aggregate {}", aggregate)
        return aggregate
    }

    private fun readAggregateFromSnapshot(
        aggregateId: UUID,
        aggregateVersion: Int? = null
    ): Aggregate? {
        return aggregateRepository.readAggregateSnapshot(aggregateId, aggregateVersion)
            ?.let { aggregate: Aggregate ->
                val snapshotVersion = aggregate.version
                log.debug("Read aggregate {} snapshot version {}", aggregateId, snapshotVersion)
                if (aggregateVersion == null || snapshotVersion < aggregateVersion) {
                    val events = eventRepository.readEvents(aggregateId, snapshotVersion, aggregateVersion)
                        .map { it.event }
                        .toList()
                    log.debug(
                        "Read {} events after version {} for aggregate {}",
                        events.size, snapshotVersion, aggregateId
                    )
                    aggregate.loadFromHistory(events)
                }
                aggregate
            }
    }

    private fun readAggregateFromEvents(
        aggregateType: AggregateType,
        aggregateId: UUID,
        aggregateVersion: Int? = null
    ): Aggregate {
        val events = eventRepository.readEvents(aggregateId, null, aggregateVersion)
            .map { it.event }
            .toList()
        log.debug("Read {} events for aggregate {}", events.size, aggregateId)
        val aggregate = aggregateType.newInstance<Aggregate>(aggregateId)
        aggregate.loadFromHistory(events)
        return aggregate
    }

    companion object {
        private val log = LoggerFactory.getLogger(AggregateStore::class.java)
    }
}
