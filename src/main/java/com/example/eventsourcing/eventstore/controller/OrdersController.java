package com.example.eventsourcing.eventstore.controller;

import com.example.eventsourcing.eventstore.domain.readmodel.Order;
import com.example.eventsourcing.eventstore.domain.writemodel.OrderStatus;
import com.example.eventsourcing.eventstore.domain.writemodel.command.AcceptOrderCommand;
import com.example.eventsourcing.eventstore.domain.writemodel.command.CancelOrderCommand;
import com.example.eventsourcing.eventstore.domain.writemodel.command.CompleteOrderCommand;
import com.example.eventsourcing.eventstore.domain.writemodel.command.PlaceOrderCommand;
import com.example.eventsourcing.eventstore.repository.OrderRepository;
import com.example.eventsourcing.eventstore.service.OrderCommandHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

  private final ObjectMapper objectMapper;
  private final OrderCommandHandler commandHandler;
  private final OrderRepository orderRepository;

  @PostMapping
  public ResponseEntity<JsonNode> placeOrder(@RequestBody JsonNode request) throws IOException {
    UUID orderId = UUID.randomUUID();
    commandHandler.process(
        PlaceOrderCommand.builder()
            .aggregateId(orderId)
            .riderId(UUID.fromString(request.get("riderId").asText()))
            .price(new BigDecimal(request.get("price").asText()))
            .route(
                objectMapper.readValue(
                    objectMapper.treeAsTokens(request.get("route")), new TypeReference<>() {}))
            .build());
    return ResponseEntity.accepted()
        .body(objectMapper.createObjectNode().put("orderId", orderId.toString()));
  }

  @PatchMapping("/{orderId}")
  public ResponseEntity<?> modifyOrder(@PathVariable UUID orderId, @RequestBody JsonNode request) {
    OrderStatus newStatus = OrderStatus.valueOf(request.get("status").asText());
    int version = request.get("version").asInt();
    switch (newStatus) {
      case ACCEPTED:
        commandHandler.process(
            AcceptOrderCommand.builder()
                .aggregateId(orderId)
                .expectedVersion(version)
                .driverId(UUID.fromString(request.get("driverId").asText()))
                .build());
        return ResponseEntity.accepted().build();
      case COMPLETED:
        commandHandler.process(new CompleteOrderCommand(orderId, version));
        return ResponseEntity.accepted().build();
      case CANCELLED:
        commandHandler.process(new CancelOrderCommand(orderId, version));
        return ResponseEntity.accepted().build();
      default:
        return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/")
  public ResponseEntity<List<Order>> getOrders() {
    return ResponseEntity.ok(orderRepository.findAll());
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<Order> getOrder(@PathVariable UUID orderId) {
    return orderRepository
        .findById(orderId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
