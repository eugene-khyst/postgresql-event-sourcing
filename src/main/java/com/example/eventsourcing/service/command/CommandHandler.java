package com.example.eventsourcing.service.command;

import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.command.Command;

public interface CommandHandler<T extends Command> {

    void handle(Aggregate aggregate, Command command);

    Class<T> getCommandType();
}
