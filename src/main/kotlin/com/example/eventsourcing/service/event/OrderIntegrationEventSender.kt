package com.example.eventsourcing.service.event

import com.example.eventsourcing.TOPIC_ORDER_EVENTS
import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.domain.OrderAggregate
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.domain.event.EventWithId
import com.example.eventsourcing.dto.OrderDto
import com.example.eventsourcing.mapper.OrderMapper
import com.example.eventsourcing.service.AggregateStore
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

class OrderIntegrationEventSender(
    private val aggregateStore: AggregateStore,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : AsyncEventHandler {
    private val orderMapper = OrderMapper()
    override fun handleEvent(eventWithId: EventWithId<Event>) {
        val event = eventWithId.event
        val aggregate = aggregateStore.readAggregate(
            AggregateType.ORDER, event.aggregateId, event.version
        )
        val orderDto = orderMapper.toDto(event, aggregate as OrderAggregate)
        sendDataToKafka(orderDto)
    }

    private fun sendDataToKafka(orderDto: OrderDto) {
        log.info("Publishing integration event {}", orderDto)
        kafkaTemplate.send(
            TOPIC_ORDER_EVENTS,
            orderDto.orderId.toString(),
            objectMapper.writeValueAsString(orderDto)
        )
    }

    override val aggregateType: AggregateType
        get() = AggregateType.ORDER

    companion object {
        private val log = LoggerFactory.getLogger(OrderIntegrationEventSender::class.java)
    }
}
