package com.example.eventsourcing.domain.event;

import com.example.eventsourcing.dto.WaypointDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
public final class OrderPlacedEvent extends Event {

    private final UUID riderId;
    private final BigDecimal price;
    private final List<WaypointDto> route;

    @JsonCreator
    @Builder
    public OrderPlacedEvent(UUID aggregateId,
                            int version,
                            UUID riderId,
                            BigDecimal price,
                            List<WaypointDto> route) {
        super(aggregateId, version);
        this.riderId = riderId;
        this.price = price;
        this.route = List.copyOf(route);
    }
}
