package com.example.eventsourcing.eventstore.service;

import com.example.eventsourcing.eventstore.eventsourcing.Event;
import com.example.eventsourcing.eventstore.eventsourcing.OptimisticConcurrencyControlError;
import com.example.eventsourcing.eventstore.repository.OrderEventRepository;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderEventStore {

  private final OrderEventRepository repository;

  public int append(Event event, int expectedVersion) {
    Objects.requireNonNull(event);
    log.debug("Appending event with expected version {}: {}", expectedVersion, event);
    UUID aggregateId = event.getAggregateId();
    repository.createAggregateIfNotExists(aggregateId);
    if (!repository.checkAndIncrementAggregateVersion(aggregateId, expectedVersion)) {
      log.debug(
          "Optimistic concurrency control error in aggregate {}: actual version doesn't match expected version {}",
          aggregateId,
          expectedVersion);
      throw new OptimisticConcurrencyControlError(expectedVersion);
    }
    repository.append(event);
    return expectedVersion + 1;
  }

  public List<Event> readEvents(UUID aggregateId) {
    Objects.requireNonNull(aggregateId);
    log.debug("Reading events for aggregate {}", aggregateId);
    List<Event> events = repository.readEvents(aggregateId);
    if (events.isEmpty()) {
      log.debug("No events for aggregate {}", aggregateId);
    }
    return events;
  }
}
