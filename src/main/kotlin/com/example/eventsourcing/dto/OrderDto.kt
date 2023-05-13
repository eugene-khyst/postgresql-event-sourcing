package com.example.eventsourcing.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

@JvmRecord
data class OrderDto(
    @field:JsonProperty("order_id") @param:JsonProperty("order_id") val orderId: UUID?,
    @field:JsonProperty(
        "event_type"
    ) @param:JsonProperty(
        "event_type"
    ) val eventType: String?,
    @field:JsonProperty("event_timestamp") @param:JsonProperty("event_timestamp") val eventTimestamp: Long,
    @field:JsonProperty(
        "version"
    ) @param:JsonProperty(
        "version"
    ) val version: Int,
    @field:JsonProperty("status") @param:JsonProperty("status") val status: OrderStatus?,
    @field:JsonProperty(
        "rider_id"
    ) @param:JsonProperty(
        "rider_id"
    ) val riderId: UUID?,
    @field:JsonProperty("price") @param:JsonProperty("price") val price: BigDecimal?,
    @field:JsonProperty(
        "route"
    ) @param:JsonProperty(
        "route"
    ) val route: List<WaypointDto?>?,
    @field:JsonProperty("driver_id") @param:JsonProperty("driver_id") val driverId: UUID?
)
