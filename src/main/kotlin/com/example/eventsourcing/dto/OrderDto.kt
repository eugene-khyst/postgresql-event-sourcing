package com.example.eventsourcing.dto

import java.math.BigDecimal
import java.util.*

data class OrderDto(
    val orderId: UUID,

    val eventType: String,

    val eventTimestamp: Long,

    val version: Int,

    val status: OrderStatus?,

    val riderId: UUID?,

    val price: BigDecimal?,

    val route: List<WaypointDto>?,

    val driverId: UUID?
)
