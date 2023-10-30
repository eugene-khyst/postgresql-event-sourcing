package eventsourcing.postgresql.domain.event;

public interface EventTypeMapper {

    Class<? extends Event> getClassByEventType(String eventType);
}
