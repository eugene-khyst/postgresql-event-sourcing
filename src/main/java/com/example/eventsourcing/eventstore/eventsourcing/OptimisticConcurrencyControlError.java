package com.example.eventsourcing.eventstore.eventsourcing;

public class OptimisticConcurrencyControlError extends Error {

  public OptimisticConcurrencyControlError(long expectedVersion) {
    super(String.format("Actual version doesn't match expected version %s", expectedVersion));
  }
}
