package com.example.eventsourcing.service.event;

import com.example.eventsourcing.projection.OrderProjection;
import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.OrderAggregate;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventWithId;
import com.example.eventsourcing.mapper.OrderMapper;
import com.example.eventsourcing.repository.OrderProjectionRepository;
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

    @Override
    public AggregateType getAggregateType() {
        return AggregateType.ORDER;
    }
}
