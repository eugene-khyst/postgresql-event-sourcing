package com.example.eventsourcing.projection

import com.example.eventsourcing.dto.OrderStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.data.domain.Persistable
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "RM_ORDER")
class OrderProjection : Persistable<UUID?> {
    @Id
    private var id: UUID? = null
    var version = 0

    @Enumerated(EnumType.STRING)
    var status: OrderStatus? = null
    var riderId: UUID? = null
    var price: BigDecimal? = null

    @ElementCollection
    @CollectionTable(name = "RM_ORDER_ROUTE", joinColumns = [JoinColumn(name = "ORDER_ID")])
    var route: List<WaypointProjection?>? = ArrayList()
    var driverId: UUID? = null
    var placedDate: Instant? = null
    var acceptedDate: Instant? = null
    var completedDate: Instant? = null
    var cancelledDate: Instant? = null

    @JsonIgnore
    override fun isNew(): Boolean {
        return version <= 1
    }

    override fun getId(): UUID? {
        return id
    }

    fun setId(id: UUID?) {
        this.id = id
    }
}
