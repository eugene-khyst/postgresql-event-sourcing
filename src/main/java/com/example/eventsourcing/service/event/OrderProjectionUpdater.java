package com.example.eventsourcing.service.event;

import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.OrderAggregate;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventWithId;
import com.example.eventsourcing.mapper.OrderMapper;
import com.example.eventsourcing.projection.OrderProjection;
import org.slf4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public class OrderProjectionUpdater implements SyncEventHandler {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderProjectionUpdater.class);
    private final JpaRepository<OrderProjection, UUID> repository;
    private final OrderMapper mapper = new OrderMapper();

    public OrderProjectionUpdater(JpaRepository<OrderProjection, UUID> repository) {
        this.repository = repository;
    }

    @Override
    public void handleEvents(List<EventWithId<Event>> events, Aggregate aggregate) {
        log.debug("Updating read model for order {}", aggregate);
        updateOrderProjection((OrderAggregate) aggregate);
    }

    private void updateOrderProjection(OrderAggregate orderAggregate) {
        OrderProjection orderProjection = mapper.toProjection(orderAggregate);
        log.info("Saving order projection {}", orderProjection);
        repository.save(orderProjection);
    }

    @Override
    public AggregateType getAggregateType() {
        return AggregateType.ORDER;
    }
}
