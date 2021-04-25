package com.example.eventsourcing.eventstore.service;

import com.example.eventsourcing.eventstore.config.OutboxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "outbox.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

  private final OutboxProperties properties;
  private final OrderEventOutboxHandler outboxHandler;

  @Scheduled(
      fixedDelayString = "${outbox.fixed-delay-ms}",
      initialDelayString = "${outbox.initial-delay-ms}")
  public void init() {
    String subscriptionName = properties.getSubscriptionName();
    try {
      outboxHandler.processNewEvents(subscriptionName);
    } catch (CannotAcquireLockException e) {
      log.debug("Can't acquire lock on subscription {}", subscriptionName);
    }
  }
}
