package com.example.eventsourcing.eventstore.domain.writemodel.event;

import com.example.eventsourcing.eventstore.eventsourcing.Event;
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
public class OrderAcceptedEvent extends Event {

  private UUID driverId;

  @Builder
  public OrderAcceptedEvent(UUID aggregateId, int version, UUID driverId) {
    super(aggregateId, version);
    this.driverId = driverId;
  }
}
