package com.example.eventsourcing.eventstore.service;

import com.example.eventsourcing.eventstore.domain.writemodel.event.OrderPlacedEvent;
import com.example.eventsourcing.eventstore.eventsourcing.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventJsonSerde {

  private final ObjectMapper objectMapper;

  @SneakyThrows
  public String serialize(Event event) {
    return objectMapper.writeValueAsString(event);
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  public <T extends Event> T deserialize(String json, String eventType) {
    return (T) objectMapper.readValue(json, toClass(eventType));
  }

  @SuppressWarnings("unchecked")
  private Class<Event> toClass(String eventType) throws ClassNotFoundException {
    return (Class<Event>) Class.forName(OrderPlacedEvent.class.getPackageName() + "." + eventType);
  }
}
