package com.example.eventsourcing.service;

import com.example.eventsourcing.service.event.AsyncEventHandler;
import org.slf4j.Logger;

import java.util.List;

public class ScheduledEventSubscriptionProcessor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ScheduledEventSubscriptionProcessor.class);
    private final List<AsyncEventHandler> eventHandlers;
    private final EventSubscriptionProcessor eventSubscriptionProcessor;

    public ScheduledEventSubscriptionProcessor(List<AsyncEventHandler> eventHandlers, EventSubscriptionProcessor eventSubscriptionProcessor) {
        this.eventHandlers = eventHandlers;
        this.eventSubscriptionProcessor = eventSubscriptionProcessor;
    }

    public void processNewEvents() {
        eventHandlers.forEach(this::processNewEvents);
    }

    private void processNewEvents(AsyncEventHandler eventHandler) {
        try {
            eventSubscriptionProcessor.processNewEvents(eventHandler);
        } catch (Exception e) {
            log.warn("Failed to handle new events for subscription %s"
                    .formatted(eventHandler.getSubscriptionName()), e);
        }
    }
}
