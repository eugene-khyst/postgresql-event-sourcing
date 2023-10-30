package com.example.eventsourcing.mapper;

import com.example.eventsourcing.domain.OrderAggregate;
import com.example.eventsourcing.dto.OrderDto;
import com.example.eventsourcing.projection.OrderProjection;
import eventsourcing.postgresql.domain.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "aggregateId", target = "id")
    OrderProjection toProjection(OrderAggregate order);

    @Mapping(source = "order.aggregateId", target = "orderId")
    @Mapping(source = "event.eventType", target = "eventType")
    @Mapping(source = "event.createdDate", target = "eventTimestamp")
    @Mapping(source = "order.baseVersion", target = "version")
    @Mapping(source = "order.riderId", target = "riderId")
    @Mapping(source = "order.price", target = "price")
    @Mapping(source = "order.route", target = "route")
    @Mapping(source = "order.driverId", target = "driverId")
    OrderDto toDto(Event event, OrderAggregate order);
}
