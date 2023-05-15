package com.example.eventsourcing.projection

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object WaypointProjectionsE : IdTable<UUID>("RM_ORDER_ROUTE") {
    override val id = reference("order_id", OrderProjectionE.id)
    val address = varchar("address", 255)
    val latitude = decimal("latitude", 10, 2)
    val longitude = decimal("longitude", 10, 2)
}

class WaypointProjectionE(orderId: EntityID<UUID>) : UUIDEntity(orderId) {
    companion object : UUIDEntityClass<WaypointProjectionE>(WaypointProjectionsE)

    var address by WaypointProjectionsE.address
    var latitude by WaypointProjectionsE.latitude
    var longitude by WaypointProjectionsE.longitude
}
