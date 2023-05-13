package com.example.eventsourcing.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

data class OrderDto(
    @JsonProperty("order_id")
    val orderId: UUID,

    @JsonProperty("event_type")
    val eventType: String,

    @JsonProperty("event_timestamp")
    val eventTimestamp: Long,

    @JsonProperty("version")
    val version: Int,

    @JsonProperty("status")
    val status: OrderStatus?,

    @JsonProperty("rider_id")
    val riderId: UUID?,

    @JsonProperty("price")
    val price: BigDecimal?,

    @JsonProperty("route")
    val route: List<WaypointDto>?,

    @JsonProperty("driver_id")
    val driverId: UUID?
)
