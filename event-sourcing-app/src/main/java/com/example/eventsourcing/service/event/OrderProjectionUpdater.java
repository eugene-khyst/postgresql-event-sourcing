package com.example.eventsourcing.service.event;

import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.OrderAggregate;
import com.example.eventsourcing.mapper.OrderMapper;
import com.example.eventsourcing.projection.OrderProjection;
import com.example.eventsourcing.repository.OrderProjectionRepository;
import eventsourcing.postgresql.domain.Aggregate;
import eventsourcing.postgresql.domain.event.Event;
import eventsourcing.postgresql.domain.event.EventWithId;
import eventsourcing.postgresql.service.event.SyncEventHandler;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProjectionUpdater implements SyncEventHandler {

    private final OrderProjectionRepository repository;
    private final OrderMapper mapper;

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

    @Nonnull
    @Override
    public String getAggregateType() {
        return AggregateType.ORDER.toString();
    }
}
