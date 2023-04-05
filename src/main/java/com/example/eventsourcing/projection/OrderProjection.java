package com.example.eventsourcing.projection;

import com.example.eventsourcing.dto.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@RequiredArgsConstructor
@Getter
@Setter
@ToString
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
    @ToString.Exclude
    private List<WaypointProjection> route = new ArrayList<>();
    private UUID driverId;
    private Instant placedDate;
    private Instant acceptedDate;
    private Instant completedDate;
    private Instant cancelledDate;

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
}
