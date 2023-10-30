package eventsourcing.postgresql.service.command;

import eventsourcing.postgresql.domain.Aggregate;
import eventsourcing.postgresql.domain.command.Command;
import jakarta.annotation.Nonnull;

public interface CommandHandler<T extends Command> {

    void handle(Aggregate aggregate, Command command);

    @Nonnull
    Class<T> getCommandType();
}
