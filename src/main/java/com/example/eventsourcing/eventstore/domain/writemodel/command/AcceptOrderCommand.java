package com.example.eventsourcing.eventstore.domain.writemodel.command;

import com.example.eventsourcing.eventstore.eventsourcing.Command;
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
public class AcceptOrderCommand extends Command {

  private UUID driverId;

  @Builder
  public AcceptOrderCommand(UUID aggregateId, int expectedVersion, UUID driverId) {
    super(aggregateId, expectedVersion);
    this.driverId = driverId;
  }
}
