package com.example.eventsourcing.error;

public class OptimisticConcurrencyControlError extends Error {

    public OptimisticConcurrencyControlError(long expectedVersion) {
        super("Actual version doesn't match expected version %s", expectedVersion);
    }
}
