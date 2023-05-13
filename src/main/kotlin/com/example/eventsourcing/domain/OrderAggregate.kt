package com.example.eventsourcing.domain

import com.example.eventsourcing.domain.command.*
import com.example.eventsourcing.domain.event.*
import com.example.eventsourcing.dto.OrderStatus
import com.example.eventsourcing.dto.WaypointDto
import com.example.eventsourcing.error.Error
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class OrderAggregate(aggregateId: UUID, version: Int) : Aggregate(aggregateId, version) {
    var status: OrderStatus? = null
        private set
    var riderId: UUID? = null
        private set
    var price: BigDecimal? = null
        private set
    var route: List<WaypointDto> = emptyList()
        private set
    var driverId: UUID? = null
        private set
    var placedDate: Instant? = null
        private set
    var acceptedDate: Instant? = null
        private set
    var completedDate: Instant? = null
        private set
    var cancelledDate: Instant? = null
        private set

    fun process(command: PlaceOrderCommand) {
        if (status != null) {
            throw Error("Can't place an order, it's already in status %s", status)
        }
        applyChange(
            OrderPlacedEvent(
                aggregateId,
                nextVersion,
                command.riderId,
                command.price,
                command.route,
            )
        )
    }

    fun process(command: AdjustOrderPriceCommand) {
        if (!EnumSet.of(OrderStatus.PLACED, OrderStatus.ADJUSTED).contains(status)) {
            throw Error("Can't adjust the price of an order in status %s", status)
        }
        applyChange(
            OrderPriceAdjustedEvent(
                aggregateId,
                nextVersion,
                command.newPrice,
            )
        )
    }

    fun process(command: AcceptOrderCommand) {
        if (EnumSet.of(OrderStatus.ACCEPTED, OrderStatus.COMPLETED, OrderStatus.CANCELLED).contains(status)) {
            throw Error("Can't accept order in status %s", status)
        }
        applyChange(
            OrderAcceptedEvent(
                aggregateId,
                nextVersion,
                command.driverId
            )
        )
    }

    fun process(command: CompleteOrderCommand) {
        if (status != OrderStatus.ACCEPTED) {
            throw Error("Order in status %s can't be completed", status)
        }
        applyChange(
            OrderCompletedEvent(
                aggregateId,
                nextVersion
            )
        )
    }

    fun process(command: CancelOrderCommand) {
        if (!EnumSet.of(OrderStatus.PLACED, OrderStatus.ADJUSTED, OrderStatus.ACCEPTED).contains(status)) {
            throw Error("Order in status %s can't be cancelled", status)
        }
        applyChange(
            OrderCancelledEvent(aggregateId, nextVersion)
        )
    }

    fun apply(event: OrderPlacedEvent) {
        status = OrderStatus.PLACED
        riderId = event.riderId
        price = event.price
        route = event.route
        placedDate = event.createdDate
    }

    fun apply(event: OrderPriceAdjustedEvent) {
        status = OrderStatus.ADJUSTED
        price = event.newPrice
    }

    fun apply(event: OrderAcceptedEvent) {
        status = OrderStatus.ACCEPTED
        driverId = event.driverId
        acceptedDate = event.createdDate
    }

    fun apply(event: OrderCompletedEvent) {
        status = OrderStatus.COMPLETED
        completedDate = event.createdDate
    }

    fun apply(event: OrderCancelledEvent) {
        status = OrderStatus.CANCELLED
        cancelledDate = event.createdDate
    }

    override val aggregateType = AggregateType.ORDER
}
