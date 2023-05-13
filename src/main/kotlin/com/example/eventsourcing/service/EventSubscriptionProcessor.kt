package com.example.eventsourcing.service

import com.example.eventsourcing.domain.event.EventSubscriptionCheckpoint
import com.example.eventsourcing.repository.EventRepository
import com.example.eventsourcing.repository.EventSubscriptionRepository
import com.example.eventsourcing.service.event.AsyncEventHandler
import org.slf4j.LoggerFactory

class EventSubscriptionProcessor(
    private val subscriptionRepository: EventSubscriptionRepository,
    private val eventRepository: EventRepository
) {
    fun processNewEvents(eventHandler: AsyncEventHandler) {
        val subscriptionName = eventHandler.subscriptionName
        log.debug("Handling new events for subscription {}", subscriptionName)
        subscriptionRepository.createSubscriptionIfAbsent(subscriptionName)
        subscriptionRepository.readCheckpointAndLockSubscription(subscriptionName).ifPresentOrElse(
            { checkpoint: EventSubscriptionCheckpoint ->
                log.debug("Acquired lock on subscription {}, checkpoint = {}", subscriptionName, checkpoint)
                val events = eventRepository.readEventsAfterCheckpoint(
                    eventHandler.aggregateType,
                    checkpoint.lastProcessedTransactionId,
                    checkpoint.lastProcessedEventId
                )
                log.debug("Fetched {} new event(s) for subscription {}", events.size, subscriptionName)
                if (!events.isEmpty()) {
                    events.forEach { eventHandler.handleEvent(it) }
                    val lastEvent = events[events.size - 1]
                    subscriptionRepository.updateEventSubscription(
                        subscriptionName, lastEvent.transactionId, lastEvent.id
                    )
                }
            }
        ) { log.debug("Can't acquire lock on subscription {}", subscriptionName) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(EventSubscriptionProcessor::class.java)
    }
}
