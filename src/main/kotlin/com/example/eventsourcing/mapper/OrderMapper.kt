package com.example.eventsourcing.mapper

import com.example.eventsourcing.domain.OrderAggregate
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.dto.OrderDto
import com.example.eventsourcing.dto.WaypointDto
import com.example.eventsourcing.projection.OrderProjection
import com.example.eventsourcing.projection.WaypointProjection
import java.time.Instant

class OrderMapper {
    fun toProjection(order: OrderAggregate): OrderProjection {
        return OrderProjection()
            .also {
                it.id = order.aggregateId
                it.version = order.version
                it.status = order.status
                it.riderId = order.riderId
                it.price = order.price
                it.route = waypointDtoListToWaypointProjectionList(order.route)
                it.driverId = order.driverId
                it.placedDate = order.placedDate
                it.acceptedDate = order.acceptedDate
                it.completedDate = order.completedDate
                it.cancelledDate = order.cancelledDate
            }
    }

    fun toDto(
        event: Event,
        order: OrderAggregate
    ): OrderDto {
        return OrderDto(
            order.aggregateId,
            event.eventType.name,
            toEpochMilli(event.createdDate),
            order.baseVersion,
            order.status,
            order.riderId,
            order.price,
            order.route,
            order.driverId
        )
    }

    private fun waypointDtoToWaypointProjection(waypointDto: WaypointDto): WaypointProjection {
        return WaypointProjection().also {
            it.address = waypointDto.address
            it.latitude = waypointDto.latitude
            it.longitude = waypointDto.longitude
        }
    }

    private fun waypointDtoListToWaypointProjectionList(list: List<WaypointDto>): List<WaypointProjection> {
        return list.map { waypointDtoToWaypointProjection(it) }
    }

    fun toEpochMilli(instant: Instant): Long {
        return instant.toEpochMilli()
    }
}
