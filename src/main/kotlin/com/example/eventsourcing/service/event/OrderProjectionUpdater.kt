package com.example.eventsourcing.service.event

import com.example.eventsourcing.domain.Aggregate
import com.example.eventsourcing.domain.AggregateType
import com.example.eventsourcing.domain.OrderAggregate
import com.example.eventsourcing.domain.event.Event
import com.example.eventsourcing.domain.event.EventWithId
import com.example.eventsourcing.mapper.OrderMapper
import com.example.eventsourcing.projection.OrderProjection
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

class OrderProjectionUpdater(private val repository: JpaRepository<OrderProjection, UUID>) : SyncEventHandler {
    private val mapper = OrderMapper()

    override fun handleEvents(events: List<EventWithId<Event>>, aggregate: Aggregate) {
        log.debug("Updating read model for order {}", aggregate)
        updateOrderProjection(aggregate as OrderAggregate)
    }

    private fun updateOrderProjection(orderAggregate: OrderAggregate) {
        val orderProjection = mapper.toProjection(orderAggregate)
        log.info("Saving order projection {}", orderProjection)
        repository.save(orderProjection)
    }

    override val aggregateType: AggregateType = AggregateType.ORDER

    companion object {
        private val log = LoggerFactory.getLogger(OrderProjectionUpdater::class.java)
    }
}
