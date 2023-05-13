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
        val orderProjection = OrderProjection()
        orderProjection.id = order.aggregateId
        orderProjection.version = order.version
        orderProjection.status = order.status
        orderProjection.riderId = order.riderId
        orderProjection.price = order.price
        orderProjection.route = waypointDtoListToWaypointProjectionList(order.route)
        orderProjection.driverId = order.driverId
        orderProjection.placedDate = order.placedDate
        orderProjection.acceptedDate = order.acceptedDate
        orderProjection.completedDate = order.completedDate
        orderProjection.cancelledDate = order.cancelledDate
        return orderProjection
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

    protected fun waypointDtoToWaypointProjection(waypointDto: WaypointDto): WaypointProjection {
        val waypointProjection = WaypointProjection()
        waypointProjection.address = waypointDto.address
        waypointProjection.latitude = waypointDto.latitude
        waypointProjection.longitude = waypointDto.longitude
        return waypointProjection
    }

    protected fun waypointDtoListToWaypointProjectionList(list: List<WaypointDto>): List<WaypointProjection> {
        val list1: MutableList<WaypointProjection> = ArrayList(list.size)
        for (waypointDto in list) {
            list1.add(waypointDtoToWaypointProjection(waypointDto))
        }
        return list1
    }

    fun toEpochMilli(instant: Instant): Long {
        return instant.toEpochMilli()
    }
}
