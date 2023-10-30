package eventsourcing.postgresql.domain.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Command {

    protected final String aggregateType;
    protected final UUID aggregateId;
}
