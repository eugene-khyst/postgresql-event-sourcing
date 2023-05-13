package com.example.eventsourcing.mapper

import com.example.eventsourcing.domain.OrderAggregate
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.dto.OrderDto
import com.example.eventsourcing.dto.OrderStatus
import com.example.eventsourcing.dto.WaypointDto
import com.example.eventsourcing.projection.OrderProjection
import com.example.eventsourcing.projection.WaypointProjection
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class OrderMapper {
    fun toProjection(order: OrderAggregate?): OrderProjection? {
        if (order == null) {
            return null
        }
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
        event: Event?,
        order: OrderAggregate?
    ): OrderDto? {
        if (event == null && order == null) {
            return null
        }
        var eventType: String? = null
        var eventTimestamp = 0L
        if (event != null) {
            if (event.eventType != null) {
                eventType = event.eventType.name
            }
            eventTimestamp = toEpochMilli(event.createdDate)
        }
        var orderId: UUID? = null
        var version = 0
        var riderId: UUID? = null
        var price: BigDecimal? = null
        var route: List<WaypointDto?>? = null
        var driverId: UUID? = null
        var status: OrderStatus? = null
        if (order != null) {
            orderId = order.aggregateId
            version = order.baseVersion
            riderId = order.riderId
            price = order.price
            val list = order.route
            if (list != null) {
                route = ArrayList(list)
            }
            driverId = order.driverId
            status = order.status
        }
        return OrderDto(orderId, eventType, eventTimestamp, version, status, riderId, price, route, driverId)
    }

    protected fun waypointDtoToWaypointProjection(waypointDto: WaypointDto?): WaypointProjection? {
        if (waypointDto == null) {
            return null
        }
        val waypointProjection = WaypointProjection()
        waypointProjection.address = waypointDto.address
        waypointProjection.latitude = waypointDto.latitude
        waypointProjection.longitude = waypointDto.longitude
        return waypointProjection
    }

    protected fun waypointDtoListToWaypointProjectionList(list: List<WaypointDto?>?): List<WaypointProjection?>? {
        if (list == null) {
            return null
        }
        val list1: MutableList<WaypointProjection?> = ArrayList(list.size)
        for (waypointDto in list) {
            list1.add(waypointDtoToWaypointProjection(waypointDto))
        }
        return list1
    }

    fun toEpochMilli(instant: Instant?): Long {
        return Optional.ofNullable(instant).map { obj: Instant -> obj.toEpochMilli() }.orElse(0L)
    }
}
