package com.example.eventsourcing.service.event;

import com.example.eventsourcing.config.KafkaTopicsConfig;
import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.OrderAggregate;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventWithId;
import com.example.eventsourcing.dto.OrderDto;
import com.example.eventsourcing.mapper.OrderMapper;
import com.example.eventsourcing.service.AggregateStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderIntegrationEventSender implements AsyncEventHandler {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderIntegrationEventSender.class);
    private final AggregateStore aggregateStore;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderIntegrationEventSender(AggregateStore aggregateStore, OrderMapper orderMapper, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.aggregateStore = aggregateStore;
        this.orderMapper = orderMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleEvent(EventWithId<Event> eventWithId) {
        Event event = eventWithId.event();
        Aggregate aggregate = aggregateStore.readAggregate(
                AggregateType.ORDER, event.getAggregateId(), event.getVersion());
        OrderDto orderDto = orderMapper.toDto(event, (OrderAggregate) aggregate);
        sendDataToKafka(orderDto);
    }

    private void sendDataToKafka(OrderDto orderDto) {
        try {
            log.info("Publishing integration event {}", orderDto);
            kafkaTemplate.send(
                    KafkaTopicsConfig.TOPIC_ORDER_EVENTS,
                    orderDto.orderId().toString(),
                    objectMapper.writeValueAsString(orderDto)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AggregateType getAggregateType() {
        return AggregateType.ORDER;
    }
}
