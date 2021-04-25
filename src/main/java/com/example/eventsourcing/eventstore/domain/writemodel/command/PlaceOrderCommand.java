package com.example.eventsourcing.eventstore.domain.writemodel.command;

import com.example.eventsourcing.eventstore.domain.writemodel.Waypoint;
import com.example.eventsourcing.eventstore.eventsourcing.Command;
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
public class PlaceOrderCommand extends Command {

  private UUID riderId;
  private BigDecimal price;
  private List<Waypoint> route;

  @Builder
  public PlaceOrderCommand(UUID aggregateId, UUID riderId, BigDecimal price, List<Waypoint> route) {
    super(aggregateId, 0);
    this.riderId = riderId;
    this.price = price;
    this.route = List.copyOf(route);
  }
}
