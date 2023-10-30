package eventsourcing.postgresql.service;

import eventsourcing.postgresql.config.EventSourcingProperties;
import eventsourcing.postgresql.config.EventSourcingProperties.SnapshottingProperties;
import eventsourcing.postgresql.domain.Aggregate;
import eventsourcing.postgresql.domain.event.Event;
import eventsourcing.postgresql.domain.event.EventWithId;
import eventsourcing.postgresql.error.OptimisticConcurrencyControlException;
import eventsourcing.postgresql.repository.AggregateRepository;
import eventsourcing.postgresql.repository.EventRepository;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Component
@RequiredArgsConstructor
@Slf4j
public class AggregateStore {

    private final AggregateRepository aggregateRepository;
    private final EventRepository eventRepository;
    private final AggregateFactory aggregateFactory;
    private final EventSourcingProperties properties;

    public List<EventWithId<Event>> saveAggregate(Aggregate aggregate) {
        log.debug("Saving aggregate {}", aggregate);

        String aggregateType = aggregate.getAggregateType();
        UUID aggregateId = aggregate.getAggregateId();
        aggregateRepository.createAggregateIfAbsent(aggregateType, aggregateId);

        int expectedVersion = aggregate.getBaseVersion();
        int newVersion = aggregate.getVersion();
        if (!aggregateRepository.checkAndUpdateAggregateVersion(aggregateId, expectedVersion, newVersion)) {
            log.warn("Optimistic concurrency control error in aggregate {} {}: " +
                     "actual version doesn't match expected version {}",
                    aggregateType, aggregateId, expectedVersion);
            throw new OptimisticConcurrencyControlException(expectedVersion);
        }

        SnapshottingProperties snapshotting = properties.getSnapshotting(aggregateType);
        List<Event> changes = aggregate.getChanges();
        List<EventWithId<Event>> newEvents = new ArrayList<>();
        for (Event event : changes) {
            log.info("Appending {} event: {}", aggregateType, event);
            EventWithId<Event> newEvent = eventRepository.appendEvent(event);
            newEvents.add(newEvent);
            createAggregateSnapshot(snapshotting, aggregate);
        }
        return newEvents;
    }

    private void createAggregateSnapshot(SnapshottingProperties snapshotting,
                                         Aggregate aggregate) {
        if (snapshotting.enabled() &&
            snapshotting.nthEvent() > 1 &&
            aggregate.getVersion() % snapshotting.nthEvent() == 0) {
            log.info("Creating {} aggregate {} version {} snapshot",
                    aggregate.getAggregateType(), aggregate.getAggregateId(), aggregate.getVersion());
            aggregateRepository.createAggregateSnapshot(aggregate);
        }
    }

    public Aggregate readAggregate(String aggregateType,
                                   UUID aggregateId) {
        return readAggregate(aggregateType, aggregateId, null);
    }

    public Aggregate readAggregate(@NonNull String aggregateType,
                                   @NonNull UUID aggregateId,
                                   @Nullable Integer version) {
        log.debug("Reading {} aggregate {}", aggregateType, aggregateId);
        SnapshottingProperties snapshotting = properties.getSnapshotting(aggregateType);
        Aggregate aggregate;
        if (snapshotting.enabled()) {
            aggregate = readAggregateFromSnapshot(aggregateId, version)
                    .orElseGet(() -> {
                        log.debug("Aggregate {} snapshot not found", aggregateId);
                        return readAggregateFromEvents(aggregateType, aggregateId, version);
                    });

        } else {
            aggregate = readAggregateFromEvents(aggregateType, aggregateId, version);
        }
        log.debug("Read aggregate {}", aggregate);
        return aggregate;
    }

    private Optional<Aggregate> readAggregateFromSnapshot(UUID aggregateId,
                                                          @Nullable Integer aggregateVersion) {
        return aggregateRepository.readAggregateSnapshot(aggregateId, aggregateVersion)
                .map(aggregate -> {
                    int snapshotVersion = aggregate.getVersion();
                    log.debug("Read aggregate {} snapshot version {}", aggregateId, snapshotVersion);
                    if (aggregateVersion == null || snapshotVersion < aggregateVersion) {
                        var events = eventRepository.readEvents(aggregateId, snapshotVersion, aggregateVersion)
                                .stream()
                                .map(EventWithId::event)
                                .toList();
                        log.debug("Read {} events after version {} for aggregate {}",
                                events.size(), snapshotVersion, aggregateId);
                        aggregate.loadFromHistory(events);
                    }
                    return aggregate;
                });
    }

    private Aggregate readAggregateFromEvents(String aggregateType,
                                              UUID aggregateId,
                                              @Nullable Integer aggregateVersion) {
        var events = eventRepository.readEvents(aggregateId, null, aggregateVersion)
                .stream()
                .map(EventWithId::event)
                .toList();
        log.debug("Read {} events for aggregate {}", events.size(), aggregateId);
        Aggregate aggregate = aggregateFactory.newInstance(aggregateType, aggregateId);
        aggregate.loadFromHistory(events);
        return aggregate;
    }
}
