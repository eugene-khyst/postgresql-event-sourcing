package eventsourcing.postgresql.domain.event;

import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public abstract class Event {

    protected final UUID aggregateId;
    protected final int version;
    protected final OffsetDateTime createdDate = OffsetDateTime.now();

    @Nonnull
    public abstract String getEventType();
}
