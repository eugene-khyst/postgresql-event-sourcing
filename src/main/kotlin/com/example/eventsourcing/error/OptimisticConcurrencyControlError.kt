package com.example.eventsourcing.error

class OptimisticConcurrencyControlError(expectedVersion: Long) :
    Error("Actual version doesn't match expected version %s", expectedVersion)
