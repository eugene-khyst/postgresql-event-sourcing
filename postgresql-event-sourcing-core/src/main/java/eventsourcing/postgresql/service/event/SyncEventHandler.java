package eventsourcing.postgresql.service.event;

import eventsourcing.postgresql.domain.Aggregate;
import eventsourcing.postgresql.domain.event.Event;
import eventsourcing.postgresql.domain.event.EventWithId;
import jakarta.annotation.Nonnull;

import java.util.List;

public interface SyncEventHandler {

    void handleEvents(List<EventWithId<Event>> events,
                      Aggregate aggregate);

    @Nonnull
    String getAggregateType();
}
