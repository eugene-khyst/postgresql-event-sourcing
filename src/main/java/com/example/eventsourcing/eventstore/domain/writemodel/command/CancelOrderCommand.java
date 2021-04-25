package com.example.eventsourcing.eventstore.domain.writemodel.command;

import com.example.eventsourcing.eventstore.eventsourcing.Command;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CancelOrderCommand extends Command {

  public CancelOrderCommand(UUID aggregateId, int expectedVersion) {
    super(aggregateId, expectedVersion);
  }
}
