package com.example.eventsourcing.eventstore.eventsourcing;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Command {

  protected UUID aggregateId;
  protected int expectedVersion;

  public String getCommandType() {
    return this.getClass().getSimpleName();
  }
}
