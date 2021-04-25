package com.example.eventsourcing.eventstore.service;

import com.example.eventsourcing.eventstore.eventsourcing.Event;
import com.example.eventsourcing.eventstore.repository.OrderEventRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderEventOutboxHandler {

  private final OrderEventRepository repository;
  private final OrderEventHandler eventHandler;

  public void processNewEvents(String subscriptionName) {
    repository.createSubscription(subscriptionName);
    long lastId = repository.readCheckpointAndAcquireLock(subscriptionName);
    log.trace("Acquired lock on subscription {}, last ID = {}", subscriptionName, lastId);
    List<Event> events = repository.readEventsAfterId(lastId);
    if (!events.isEmpty()) {
      log.debug("Fetched {} new event(s) for subscription {}", events.size(), subscriptionName);
    } else {
      log.trace("Fetched 0 new event(s) for subscription {}", subscriptionName);
    }
    for (Event event : events) {
      eventHandler.process(event);
      lastId = event.getId();
    }
    repository.updateCheckpoint(subscriptionName, lastId);
  }
}
