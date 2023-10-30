package com.example.eventsourcing.service.command;

import com.example.eventsourcing.domain.command.PlaceOrderCommand;
import eventsourcing.postgresql.domain.Aggregate;
import eventsourcing.postgresql.domain.command.Command;
import eventsourcing.postgresql.service.command.CommandHandler;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlaceOrderCommandHandler implements CommandHandler<PlaceOrderCommand> {

    @Override
    public void handle(Aggregate aggregate, Command command) {
        // Add additional business logic here.
        aggregate.process(command);
        // Also, add additional business logic here.
        // Read other aggregates using AggregateStore.
    }

    @Nonnull
    @Override
    public Class<PlaceOrderCommand> getCommandType() {
        return PlaceOrderCommand.class;
    }
}
