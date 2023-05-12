package com.example.eventsourcing.service.command;

import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.command.Command;
import com.example.eventsourcing.domain.command.PlaceOrderCommand;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PlaceOrderCommandHandler implements CommandHandler<PlaceOrderCommand> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PlaceOrderCommandHandler.class);

    @Override
    public void handle(Aggregate aggregate, Command command) {
        // Add additional business logic here.
        aggregate.process(command);
        // Also, add additional business logic here.
        // Read other aggregates using AggregateStore.
    }

    @Override
    public Class<PlaceOrderCommand> getCommandType() {
        return PlaceOrderCommand.class;
    }
}
