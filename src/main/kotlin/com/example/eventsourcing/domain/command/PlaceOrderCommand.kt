package com.example.eventsourcing.domain.command;

import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.dto.WaypointDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class PlaceOrderCommand extends Command {

    private final UUID riderId;
    private final BigDecimal price;
    private final List<WaypointDto> route;

    public PlaceOrderCommand(UUID riderId,
                             BigDecimal price,
                             List<WaypointDto> route) {
        super(AggregateType.ORDER, generateAggregateId());
        this.riderId = riderId;
        this.price = price;
        this.route = route;
    }

    private static UUID generateAggregateId() {
        return UUID.randomUUID();
    }

    public UUID getRiderId() {
        return this.riderId;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public List<WaypointDto> getRoute() {
        return this.route;
    }

    public String toString() {
        return "PlaceOrderCommand(super=" + super.toString() + ", riderId=" + this.getRiderId() + ", price=" + this.getPrice() + ", route=" + this.getRoute() + ")";
    }
}
