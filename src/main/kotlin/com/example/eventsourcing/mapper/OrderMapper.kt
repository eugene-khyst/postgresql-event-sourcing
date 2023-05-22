package com.example.eventsourcing.mapper

import com.example.eventsourcing.domain.OrderAggregate
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.dto.OrderDto
import com.example.eventsourcing.dto.WaypointDto
import com.example.eventsourcing.projection.OrderProjection
import com.example.eventsourcing.projection.WaypointProjection

class OrderMapper {
    fun toProjection(order: OrderAggregate): OrderProjection = OrderProjection()
        .also {
            it.id = order.aggregateId
            it.version = order.version
            it.status = order.status
            it.riderId = order.riderId
            it.price = order.price
            it.route = order.route.map { waypointDtoToWaypointProjection(it) }
            it.driverId = order.driverId
            it.placedDate = order.placedDate
            it.acceptedDate = order.acceptedDate
            it.completedDate = order.completedDate
            it.cancelledDate = order.cancelledDate
        }

    fun toDto(
        event: Event,
        order: OrderAggregate
    ): OrderDto = OrderDto(
        order.aggregateId,
        event.eventType.name,
        event.createdAt.toEpochMilli(),
        order.baseVersion,
        order.status,
        order.riderId,
        order.price,
        order.route,
        order.driverId
    )

    private fun waypointDtoToWaypointProjection(waypointDto: WaypointDto): WaypointProjection =
        WaypointProjection().also {
            it.address = waypointDto.address
            it.latitude = waypointDto.latitude
            it.longitude = waypointDto.longitude
        }
}
