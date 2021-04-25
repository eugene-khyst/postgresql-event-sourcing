package com.example.eventsourcing.eventstore.domain.writemodel.event;

import com.example.eventsourcing.eventstore.domain.writemodel.Waypoint;
import com.example.eventsourcing.eventstore.eventsourcing.Event;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrderPlacedEvent extends Event {

  private UUID riderId;
  private BigDecimal price;
  private List<Waypoint> route;

  @Builder
  public OrderPlacedEvent(
      UUID aggregateId, int version, UUID riderId, BigDecimal price, List<Waypoint> route) {
    super(aggregateId, version);
    this.riderId = riderId;
    this.price = price;
    this.route = route;
  }
}
