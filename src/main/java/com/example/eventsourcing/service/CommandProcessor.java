package com.example.eventsourcing.service;

import com.example.eventsourcing.domain.Aggregate;
import com.example.eventsourcing.domain.AggregateType;
import com.example.eventsourcing.domain.command.Command;
import com.example.eventsourcing.domain.event.Event;
import com.example.eventsourcing.domain.event.EventWithId;
import com.example.eventsourcing.service.command.CommandHandler;
import com.example.eventsourcing.service.command.DefaultCommandHandler;
import com.example.eventsourcing.service.event.SyncEventHandler;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

public class CommandProcessor {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CommandProcessor.class);
    private final AggregateStore aggregateStore;
    private final List<CommandHandler<? extends Command>> commandHandlers;
    private final DefaultCommandHandler defaultCommandHandler;
    private final List<SyncEventHandler> aggregateChangesHandlers;

    public CommandProcessor(AggregateStore aggregateStore, List<CommandHandler<? extends Command>> commandHandlers, DefaultCommandHandler defaultCommandHandler, List<SyncEventHandler> aggregateChangesHandlers) {
        this.aggregateStore = aggregateStore;
        this.commandHandlers = commandHandlers;
        this.defaultCommandHandler = defaultCommandHandler;
        this.aggregateChangesHandlers = aggregateChangesHandlers;
    }

    public Aggregate process(Command command) {
        log.debug("Processing command {}", command);

        AggregateType aggregateType = command.getAggregateType();
        UUID aggregateId = command.getAggregateId();

        Aggregate aggregate = aggregateStore.readAggregate(aggregateType, aggregateId);

        commandHandlers.stream()
                .filter(commandHandler -> commandHandler.getCommandType() == command.getClass())
                .findFirst()
                .ifPresentOrElse(commandHandler -> {
                    log.debug("Handling command {} with {}",
                            command.getClass().getSimpleName(), commandHandler.getClass().getSimpleName());
                    commandHandler.handle(aggregate, command);
                }, () -> {
                    log.debug("No specialized handler found, handling command {} with {}",
                            command.getClass().getSimpleName(), defaultCommandHandler.getClass().getSimpleName());
                    defaultCommandHandler.handle(aggregate, command);
                });

        List<EventWithId<Event>> newEvents = aggregateStore.saveAggregate(aggregate);

        aggregateChangesHandlers.stream()
                .filter(handler -> handler.getAggregateType() == aggregateType)
                .forEach(handler -> handler.handleEvents(newEvents, aggregate));

        return aggregate;
    }
}
