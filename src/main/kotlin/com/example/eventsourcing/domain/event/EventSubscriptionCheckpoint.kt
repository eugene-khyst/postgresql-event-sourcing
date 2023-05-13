package com.example.eventsourcing.domain.event

import java.math.BigInteger

data class EventSubscriptionCheckpoint(val lastProcessedTransactionId: BigInteger, val lastProcessedEventId: Long)
