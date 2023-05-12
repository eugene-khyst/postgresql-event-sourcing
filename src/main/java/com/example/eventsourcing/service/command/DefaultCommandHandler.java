package com.example.eventsourcing.service.command;

import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.command.Command;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DefaultCommandHandler implements CommandHandler<Command> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DefaultCommandHandler.class);

    @Override
    public void handle(Aggregate aggregate, Command command) {
        aggregate.process(command);
    }

    @Override
    public Class<Command> getCommandType() {
        return Command.class;
    }
}
