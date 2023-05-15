package com.example.eventsourcing.projection

import com.example.eventsourcing.dto.OrderStatus
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object OrderProjectionE : IdTable<UUID>("RM_ORDER") {
    override val id = uuid("id").entityId()
    val version = integer("version")
    val status = enumeration("status", OrderStatus::class)
    val riderId = uuid("rider_id")
    val price = decimal("price", 10, 2)
    val driverId = uuid("driver_id").nullable()
    val placedDate = datetime("placed_date")
    val acceptedDate = datetime("accepted_date").nullable()
    val completedDate = datetime("completed_date").nullable()
    val cancelledDate = datetime("cancelled_date").nullable()
}

class OrderProjectionEn(orderId: EntityID<UUID>) : UUIDEntity(orderId) {
    companion object : UUIDEntityClass<OrderProjectionEn>(OrderProjectionE)

    var version by OrderProjectionE.version
    var status by OrderProjectionE.status
    var riderId by OrderProjectionE.riderId
    var price by OrderProjectionE.price
    var driverId by OrderProjectionE.driverId
    var placedDate by OrderProjectionE.placedDate
    var acceptedDate by OrderProjectionE.acceptedDate
    var completedDate by OrderProjectionE.completedDate
    var cancelledDate by OrderProjectionE.cancelledDate
    val route by WaypointProjectionE.backReferencedOn(WaypointProjectionsE.id)
}
