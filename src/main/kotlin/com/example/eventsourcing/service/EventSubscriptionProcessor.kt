package com.example.eventsourcing.service;

import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventWithId;
import com.example.eventsourcing.repository.EventRepository;
import com.example.eventsourcing.repository.EventSubscriptionRepository;
import com.example.eventsourcing.service.event.AsyncEventHandler;
import org.slf4j.Logger;

import java.util.List;

public class EventSubscriptionProcessor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(EventSubscriptionProcessor.class);
    private final EventSubscriptionRepository subscriptionRepository;
    private final EventRepository eventRepository;

    public EventSubscriptionProcessor(EventSubscriptionRepository subscriptionRepository, EventRepository eventRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.eventRepository = eventRepository;
    }

    public void processNewEvents(AsyncEventHandler eventHandler) {
        String subscriptionName = eventHandler.getSubscriptionName();
        log.debug("Handling new events for subscription {}", subscriptionName);

        subscriptionRepository.createSubscriptionIfAbsent(subscriptionName);
        subscriptionRepository.readCheckpointAndLockSubscription(subscriptionName).ifPresentOrElse(
                checkpoint -> {
                    log.debug("Acquired lock on subscription {}, checkpoint = {}", subscriptionName, checkpoint);
                    List<EventWithId<Event>> events = eventRepository.readEventsAfterCheckpoint(
                            eventHandler.getAggregateType(),
                            checkpoint.lastProcessedTransactionId(),
                            checkpoint.lastProcessedEventId()
                    );
                    log.debug("Fetched {} new event(s) for subscription {}", events.size(), subscriptionName);
                    if (!events.isEmpty()) {
                        events.forEach(eventHandler::handleEvent);
                        EventWithId<Event> lastEvent = events.get(events.size() - 1);
                        subscriptionRepository.updateEventSubscription(
                                subscriptionName, lastEvent.transactionId(), lastEvent.id());
                    }
                },
                () -> log.debug("Can't acquire lock on subscription {}", subscriptionName));
    }
}
