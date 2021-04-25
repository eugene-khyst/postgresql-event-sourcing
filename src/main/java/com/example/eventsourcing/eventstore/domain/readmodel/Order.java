package com.example.eventsourcing.eventstore.domain.readmodel;

import com.example.eventsourcing.eventstore.domain.writemodel.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "ORDERS")
@Data
@EqualsAndHashCode(exclude = "id")
public class Order implements Persistable<UUID>, Serializable {

  @Id private UUID id;
  private int version;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private UUID riderId;
  private BigDecimal price;
  @ElementCollection private List<Waypoint> route = new ArrayList<>();
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
}
