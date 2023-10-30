package eventsourcing.postgresql.error;

import lombok.NonNull;

public class AggregateStateException extends RuntimeException {

    public AggregateStateException(@NonNull String message, Object... args) {
        super(message.formatted(args));
    }
}
