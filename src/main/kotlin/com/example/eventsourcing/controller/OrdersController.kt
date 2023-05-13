package com.example.eventsourcing.controller

import com.example.eventsourcing.domain.command.*
import com.example.eventsourcing.dto.OrderStatus
import com.example.eventsourcing.dto.WaypointDto
import com.example.eventsourcing.projection.OrderProjection
import com.example.eventsourcing.service.CommandProcessor
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.util.*

class OrdersController(
    private val objectMapper: ObjectMapper,
    private val commandProcessor: CommandProcessor,
    private val orderProjectionRepository: JpaRepository<OrderProjection, UUID>
) {
    fun placeOrder(request: JsonNode): ResponseEntity<JsonNode> {
        val order = commandProcessor.process(PlaceOrderCommand(
            UUID.fromString(request["riderId"].asText()),
            BigDecimal(request["price"].asText()),
            objectMapper.readValue(
                objectMapper.treeAsTokens(request["route"]), object : TypeReference<List<WaypointDto>>() {}
            )))
        return ResponseEntity.ok()
            .body(
                objectMapper.createObjectNode()
                    .put("orderId", order.aggregateId.toString())
            )
    }

    fun modifyOrder(orderId: UUID, request: JsonNode): ResponseEntity<Any> {
        val newStatus = OrderStatus.valueOf(request["status"].asText())
        return when (newStatus) {
            OrderStatus.ADJUSTED -> {
                commandProcessor.process(
                    AdjustOrderPriceCommand(
                        orderId,
                        BigDecimal(request["price"].asText())
                    )
                )
                ResponseEntity.ok().build()
            }

            OrderStatus.ACCEPTED -> {
                commandProcessor.process(
                    AcceptOrderCommand(
                        orderId,
                        UUID.fromString(request["driverId"].asText())
                    )
                )
                ResponseEntity.ok().build()
            }

            OrderStatus.COMPLETED -> {
                commandProcessor.process(CompleteOrderCommand(orderId))
                ResponseEntity.ok().build()
            }

            OrderStatus.CANCELLED -> {
                commandProcessor.process(CancelOrderCommand(orderId))
                ResponseEntity.ok().build()
            }

            else -> {
                ResponseEntity.badRequest().build()
            }
        }
    }

    val orders: ResponseEntity<List<OrderProjection>>
        get() = ResponseEntity.ok(orderProjectionRepository.findAll())

    fun getOrder(orderId: UUID): ResponseEntity<OrderProjection> {
        return orderProjectionRepository
            .findById(orderId)
            .map<ResponseEntity<OrderProjection>> { body: OrderProjection? -> ResponseEntity.ok(body) }
            .orElse(ResponseEntity.notFound().build())
    }
}
