package com.example.eventsourcing.controller;

import com.example.eventsourcing.domain.command.*;
import com.example.eventsourcing.dto.OrderStatus;
import com.example.eventsourcing.projection.OrderProjection;
import com.example.eventsourcing.service.CommandProcessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrdersController {

    private final ObjectMapper objectMapper;
    private final CommandProcessor commandProcessor;
    private final JpaRepository<OrderProjection, UUID> orderProjectionRepository;

    public OrdersController(ObjectMapper objectMapper, CommandProcessor commandProcessor, JpaRepository<OrderProjection, UUID> orderProjectionRepository) {
        this.objectMapper = objectMapper;
        this.commandProcessor = commandProcessor;
        this.orderProjectionRepository = orderProjectionRepository;
    }

    public ResponseEntity<JsonNode> placeOrder(JsonNode request) throws IOException {
        var order = commandProcessor.process(new PlaceOrderCommand(
                UUID.fromString(request.get("riderId").asText()),
                new BigDecimal(request.get("price").asText()),
                objectMapper.readValue(
                        objectMapper.treeAsTokens(request.get("route")), new TypeReference<>() {
                        }
                )));
        return ResponseEntity.ok()
                .body(objectMapper.createObjectNode()
                        .put("orderId", order.getAggregateId().toString()));
    }

    public ResponseEntity<Object> modifyOrder(UUID orderId, JsonNode request) {
        OrderStatus newStatus = OrderStatus.valueOf(request.get("status").asText());
        switch (newStatus) {
            case ADJUSTED -> {
                commandProcessor.process(new AdjustOrderPriceCommand(
                        orderId,
                        new BigDecimal(request.get("price").asText())
                ));
                return ResponseEntity.ok().build();
            }
            case ACCEPTED -> {
                commandProcessor.process(new AcceptOrderCommand(
                        orderId,
                        UUID.fromString(request.get("driverId").asText())
                ));
                return ResponseEntity.ok().build();
            }
            case COMPLETED -> {
                commandProcessor.process(new CompleteOrderCommand(orderId));
                return ResponseEntity.ok().build();
            }
            case CANCELLED -> {
                commandProcessor.process(new CancelOrderCommand(orderId));
                return ResponseEntity.ok().build();
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    public ResponseEntity<List<OrderProjection>> getOrders() {
        return ResponseEntity.ok(orderProjectionRepository.findAll());
    }

    public ResponseEntity<OrderProjection> getOrder(UUID orderId) {
        return orderProjectionRepository
                .findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
