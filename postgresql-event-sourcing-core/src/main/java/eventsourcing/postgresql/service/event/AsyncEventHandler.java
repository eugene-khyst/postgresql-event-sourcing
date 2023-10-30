package eventsourcing.postgresql.service.event;

import eventsourcing.postgresql.domain.event.Event;
import eventsourcing.postgresql.domain.event.EventWithId;
import jakarta.annotation.Nonnull;

public interface AsyncEventHandler {

    void handleEvent(EventWithId<Event> event);

    @Nonnull
    String getAggregateType();

    default String getSubscriptionName() {
        return getClass().getName();
    }
}
