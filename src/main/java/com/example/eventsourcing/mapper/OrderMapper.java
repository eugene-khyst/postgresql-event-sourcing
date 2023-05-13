package com.example.eventsourcing.mapper;

import com.example.eventsourcing.domain.OrderAggregate;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.dto.OrderDto;
import com.example.eventsourcing.dto.OrderStatus;
import com.example.eventsourcing.dto.WaypointDto;
import com.example.eventsourcing.projection.OrderProjection;
import com.example.eventsourcing.projection.WaypointProjection;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderMapper {

    public OrderProjection toProjection(OrderAggregate order) {
        if (order == null) {
            return null;
        }

        OrderProjection orderProjection = new OrderProjection();

        orderProjection.setId(order.getAggregateId());
        orderProjection.setVersion(order.getVersion());
        orderProjection.setStatus(order.getStatus());
        orderProjection.setRiderId(order.getRiderId());
        orderProjection.setPrice(order.getPrice());
        orderProjection.setRoute(waypointDtoListToWaypointProjectionList(order.getRoute()));
        orderProjection.setDriverId(order.getDriverId());
        orderProjection.setPlacedDate(order.getPlacedDate());
        orderProjection.setAcceptedDate(order.getAcceptedDate());
        orderProjection.setCompletedDate(order.getCompletedDate());
        orderProjection.setCancelledDate(order.getCancelledDate());

        return orderProjection;
    }

    public OrderDto toDto(Event event, OrderAggregate order) {
        if (event == null && order == null) {
            return null;
        }

        String eventType = null;
        long eventTimestamp = 0L;
        if (event != null) {
            if (event.getEventType() != null) {
                eventType = event.getEventType().name();
            }
            eventTimestamp = toEpochMilli(event.getCreatedDate());
        }
        UUID orderId = null;
        int version = 0;
        UUID riderId = null;
        BigDecimal price = null;
        List<WaypointDto> route = null;
        UUID driverId = null;
        OrderStatus status = null;
        if (order != null) {
            orderId = order.getAggregateId();
            version = order.getBaseVersion();
            riderId = order.getRiderId();
            price = order.getPrice();
            List<WaypointDto> list = order.getRoute();
            if (list != null) {
                route = new ArrayList<WaypointDto>(list);
            }
            driverId = order.getDriverId();
            status = order.getStatus();
        }

        OrderDto orderDto = new OrderDto(orderId, eventType, eventTimestamp, version, status, riderId, price, route, driverId);

        return orderDto;
    }

    protected WaypointProjection waypointDtoToWaypointProjection(WaypointDto waypointDto) {
        if (waypointDto == null) {
            return null;
        }

        WaypointProjection waypointProjection = new WaypointProjection();

        waypointProjection.setAddress(waypointDto.address());
        waypointProjection.setLatitude(waypointDto.latitude());
        waypointProjection.setLongitude(waypointDto.longitude());

        return waypointProjection;
    }

    protected List<WaypointProjection> waypointDtoListToWaypointProjectionList(List<WaypointDto> list) {
        if (list == null) {
            return null;
        }

        List<WaypointProjection> list1 = new ArrayList<WaypointProjection>(list.size());
        for (WaypointDto waypointDto : list) {
            list1.add(waypointDtoToWaypointProjection(waypointDto));
        }

        return list1;
    }

    long toEpochMilli(Instant instant) {
        return Optional.ofNullable(instant).map(Instant::toEpochMilli).orElse(0L);
    }
}
