package com.example.eventsourcing.eventstore.service;

import com.example.eventsourcing.eventstore.domain.writemodel.Order;
import com.example.eventsourcing.eventstore.eventsourcing.Command;
import com.example.eventsourcing.eventstore.eventsourcing.Event;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandHandler {

  private final OrderEventStore eventStore;

  public void process(Command command) {
    Objects.requireNonNull(command);
    log.debug("Processing command {}", command);
    UUID orderId = command.getAggregateId();
    List<Event> events = eventStore.readEvents(orderId);
    Order order = new Order(orderId, events);
    order.process(command);
    int expectedVersion = command.getExpectedVersion();
    for (Event event : order.getChanges()) {
      expectedVersion = eventStore.append(event, expectedVersion);
    }
  }
}
