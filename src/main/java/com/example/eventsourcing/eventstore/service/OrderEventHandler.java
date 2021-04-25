package com.example.eventsourcing.eventstore.service;

import com.example.eventsourcing.eventstore.domain.writemodel.Order;
import com.example.eventsourcing.eventstore.eventsourcing.Event;
import com.example.eventsourcing.eventstore.mapper.OrderMapper;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

  private final OrderEventStore eventStore;
  private final OrderMapper mapper;
  private final OrderReadModelUpdater readModelUpdater;
  private final OrderIntegrationEventSender integrationEventSender;

  public void process(Event event) {
    Objects.requireNonNull(event);
    log.debug("Processing event {}", event);
    UUID orderId = event.getAggregateId();
    List<Event> events = eventStore.readEvents(orderId);
    Order order = new Order(orderId, events);
    readModelUpdater.saveOrUpdate(mapper.toReadModel(order));
    integrationEventSender.send(mapper.toIntegrationEvent(event, order));
  }
}
