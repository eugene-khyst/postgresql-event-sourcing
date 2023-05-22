package com.example.eventsourcing.controller

import com.example.eventsourcing.domain.command.AcceptOrderCommand
import com.example.eventsourcing.domain.command.AdjustOrderPriceCommand
import com.example.eventsourcing.domain.command.CancelOrderCommand
import com.example.eventsourcing.domain.command.CompleteOrderCommand
import com.example.eventsourcing.domain.command.PlaceOrderCommand
import com.example.eventsourcing.dto.OrderStatus
import com.example.eventsourcing.dto.WaypointDto
import com.example.eventsourcing.projection.OrderProjection
import com.example.eventsourcing.service.CommandProcessor
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.time.Clock
import java.util.*

class OrdersController(
    private val clock: Clock,
    private val objectMapper: ObjectMapper,
    private val commandProcessor: CommandProcessor,
    private val orderProjectionRepository: JpaRepository<OrderProjection, UUID>
) {
    fun placeOrder(request: JsonNode): Response {
        val order = commandProcessor.process(PlaceOrderCommand(
            UUID.fromString(request["riderId"].asText()),
            BigDecimal(request["price"].asText()),
            objectMapper.readValue(
                objectMapper.treeAsTokens(request["route"]), object : TypeReference<List<WaypointDto>>() {}
            ), clock.instant()))
        return Response(OK)
            .with(
                bodyOf(
                    objectMapper.createObjectNode()
                        .put("orderId", order.aggregateId.toString())
                )
            )
    }

    fun modifyOrder(orderId: UUID, request: JsonNode): Response {
        return when (OrderStatus.valueOf(request["status"].asText())) {
            OrderStatus.ADJUSTED -> {
                commandProcessor.process(
                    AdjustOrderPriceCommand(
                        orderId,
                        clock.instant(),
                        BigDecimal(request["price"].asText())
                    )
                )
                Response(OK)
            }

            OrderStatus.ACCEPTED -> {
                commandProcessor.process(
                    AcceptOrderCommand(
                        orderId,
                        clock.instant(),
                        UUID.fromString(request["driverId"].asText())
                    )
                )
                Response(OK)
            }

            OrderStatus.COMPLETED -> {
                commandProcessor.process(
                    CompleteOrderCommand(
                        orderId, clock.instant()
                    )
                )
                Response(OK)
            }

            OrderStatus.CANCELLED -> {
                commandProcessor.process(
                    CancelOrderCommand(
                        orderId, clock.instant(),
                    )
                )
                Response(OK)
            }

            else -> {
                Response(BAD_REQUEST)
            }
        }
    }

    fun orders(): Response = Response(OK).with(bodyOf(orderProjectionRepository.findAll()))

    fun getOrder(orderId: UUID): Response {
        return orderProjectionRepository
            .findById(orderId)
            .map { body: OrderProjection -> Response(OK).with(bodyOf(body)) }
            .orElse(Response(NOT_FOUND))
    }

    private inline fun <reified T : Any> bodyOf(body: T): (Response) -> Response = Body.auto<T>().toLens() of body
}
