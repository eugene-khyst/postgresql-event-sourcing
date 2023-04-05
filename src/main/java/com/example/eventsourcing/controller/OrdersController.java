package com.example.eventsourcing.controller;

import com.example.eventsourcing.domain.command.*;
import com.example.eventsourcing.projection.OrderProjection;
import com.example.eventsourcing.dto.OrderStatus;
import com.example.eventsourcing.repository.OrderProjectionRepository;
import com.example.eventsourcing.service.CommandProcessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final ObjectMapper objectMapper;
    private final CommandProcessor commandProcessor;
    private final OrderProjectionRepository orderProjectionRepository;

    @PostMapping
    public ResponseEntity<JsonNode> placeOrder(@RequestBody JsonNode request) throws IOException {
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

    @PutMapping("/{orderId}")
    public ResponseEntity<Object> modifyOrder(@PathVariable UUID orderId, @RequestBody JsonNode request) {
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

    @GetMapping("/")
    public ResponseEntity<List<OrderProjection>> getOrders() {
        return ResponseEntity.ok(orderProjectionRepository.findAll());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderProjection> getOrder(@PathVariable UUID orderId) {
        return orderProjectionRepository
                .findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
