package com.example.eventsourcing.domain;

import com.example.eventsourcing.domain.command.*;
import com.example.eventsourcing.domain.event.*;
import com.example.eventsourcing.dto.OrderStatus;
import com.example.eventsourcing.dto.WaypointDto;
import com.example.eventsourcing.error.Error;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class OrderAggregate extends Aggregate {

    private OrderStatus status;
    private UUID riderId;
    private BigDecimal price;
    private List<WaypointDto> route;
    private UUID driverId;
    private Instant placedDate;
    private Instant acceptedDate;
    private Instant completedDate;
    private Instant cancelledDate;

    @JsonCreator
    public OrderAggregate(UUID aggregateId, int version) {
        super(aggregateId, version);
    }

    public void process(PlaceOrderCommand command) {
        if (status != null) {
            throw new Error("Can't place an order, it's already in status %s", status);
        }
        applyChange(OrderPlacedEvent.builder()
                .aggregateId(aggregateId)
                .version(getNextVersion())
                .riderId(command.getRiderId())
                .price(command.getPrice())
                .route(command.getRoute())
                .build());
    }

    public void process(AdjustOrderPriceCommand command) {
        if (!EnumSet.of(OrderStatus.PLACED, OrderStatus.ADJUSTED).contains(status)) {
            throw new Error("Can't adjust the price of an order in status %s", status);
        }
        applyChange(OrderPriceAdjustedEvent.builder()
                .aggregateId(aggregateId)
                .version(getNextVersion())
                .newPrice(command.getNewPrice())
                .build());
    }

    public void process(AcceptOrderCommand command) {
        if (EnumSet.of(OrderStatus.ACCEPTED, OrderStatus.COMPLETED, OrderStatus.CANCELLED).contains(status)) {
            throw new Error("Can't accept order in status %s", status);
        }
        applyChange(OrderAcceptedEvent.builder()
                .aggregateId(aggregateId)
                .version(getNextVersion())
                .driverId(command.getDriverId())
                .build());
    }

    public void process(CompleteOrderCommand command) {
        if (status != OrderStatus.ACCEPTED) {
            throw new Error("Order in status %s can't be completed", status);
        }
        applyChange(OrderCompletedEvent.builder()
                .aggregateId(aggregateId)
                .version(getNextVersion())
                .build());
    }

    public void process(CancelOrderCommand command) {
        if (!EnumSet.of(OrderStatus.PLACED, OrderStatus.ADJUSTED, OrderStatus.ACCEPTED).contains(status)) {
            throw new Error("Order in status %s can't be cancelled", status);
        }
        applyChange(OrderCancelledEvent.builder()
                .aggregateId(aggregateId)
                .version(getNextVersion())
                .build());
    }

    public void apply(OrderPlacedEvent event) {
        this.status = OrderStatus.PLACED;
        this.riderId = event.getRiderId();
        this.price = event.getPrice();
        this.route = event.getRoute();
        this.placedDate = event.getCreatedDate();
    }

    public void apply(OrderPriceAdjustedEvent event) {
        this.status = OrderStatus.ADJUSTED;
        this.price = event.getNewPrice();
    }

    public void apply(OrderAcceptedEvent event) {
        this.status = OrderStatus.ACCEPTED;
        this.driverId = event.getDriverId();
        this.acceptedDate = event.getCreatedDate();
    }

    public void apply(OrderCompletedEvent event) {
        this.status = OrderStatus.COMPLETED;
        this.completedDate = event.getCreatedDate();
    }

    public void apply(OrderCancelledEvent event) {
        this.status = OrderStatus.CANCELLED;
        this.cancelledDate = event.getCreatedDate();
    }

    @Override
    public AggregateType getAggregateType() {
        return AggregateType.ORDER;
    }

    public OrderStatus getStatus() {
        return this.status;
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

    public UUID getDriverId() {
        return this.driverId;
    }

    public Instant getPlacedDate() {
        return this.placedDate;
    }

    public Instant getAcceptedDate() {
        return this.acceptedDate;
    }

    public Instant getCompletedDate() {
        return this.completedDate;
    }

    public Instant getCancelledDate() {
        return this.cancelledDate;
    }

    public String toString() {
        return "OrderAggregate(super=" + super.toString() + ", status=" + this.getStatus() + ", riderId=" + this.getRiderId() + ", price=" + this.getPrice() + ", route=" + this.getRoute() + ", driverId=" + this.getDriverId() + ", placedDate=" + this.getPlacedDate() + ", acceptedDate=" + this.getAcceptedDate() + ", completedDate=" + this.getCompletedDate() + ", cancelledDate=" + this.getCancelledDate() + ")";
    }
}
