package com.example.eventsourcing.projection;

import com.example.eventsourcing.dto.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "RM_ORDER")
public class OrderProjection implements Persistable<UUID>, Serializable {

    @Id
    private UUID id;
    private int version;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private UUID riderId;
    private BigDecimal price;
    @ElementCollection
    @CollectionTable(name = "RM_ORDER_ROUTE", joinColumns = @JoinColumn(name = "ORDER_ID"))
    private List<WaypointProjection> route = new ArrayList<>();
    private UUID driverId;
    private Instant placedDate;
    private Instant acceptedDate;
    private Instant completedDate;
    private Instant cancelledDate;

    public OrderProjection() {
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return version <= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        OrderProjection other = (OrderProjection) o;
        return Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public UUID getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
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

    public List<WaypointProjection> getRoute() {
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

    public void setId(UUID id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setRiderId(UUID riderId) {
        this.riderId = riderId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setRoute(List<WaypointProjection> route) {
        this.route = route;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public void setAcceptedDate(Instant acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public void setCompletedDate(Instant completedDate) {
        this.completedDate = completedDate;
    }

    public void setCancelledDate(Instant cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String toString() {
        return "OrderProjection(id=" + this.getId() + ", version=" + this.getVersion() + ", status=" + this.getStatus() + ", riderId=" + this.getRiderId() + ", price=" + this.getPrice() + ", driverId=" + this.getDriverId() + ", placedDate=" + this.getPlacedDate() + ", acceptedDate=" + this.getAcceptedDate() + ", completedDate=" + this.getCompletedDate() + ", cancelledDate=" + this.getCancelledDate() + ")";
    }
}
