package com.example.eventsourcing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        @JsonProperty("order_id")
        UUID orderId,
        @JsonProperty("event_type")
        String eventType,
        @JsonProperty("event_timestamp")
        long eventTimestamp,
        @JsonProperty("version")
        int version,
        @JsonProperty("status")
        OrderStatus status,
        @JsonProperty("rider_id")
        UUID riderId,
        @JsonProperty("price")
        BigDecimal price,
        @JsonProperty("route")
        List<WaypointDto> route,
        @JsonProperty("driver_id")
        UUID driverId
) {
}
