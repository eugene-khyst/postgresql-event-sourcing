package com.example.eventsourcing.eventstore.service;

import com.example.eventsourcing.eventstore.config.KafkaTopicsConfig;
import com.example.eventsourcing.eventstore.domain.integration.OrderIntegrationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderIntegrationEventSender {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @SneakyThrows
  public void send(OrderIntegrationEvent event) {
    Objects.requireNonNull(event);
    log.debug("Publishing integration event {}", event);
    kafkaTemplate.send(
        KafkaTopicsConfig.TOPIC_ORDER_INTEGRATION_EVENTS,
        event.getOrderId().toString(),
        objectMapper.writeValueAsString(event));
  }
}
