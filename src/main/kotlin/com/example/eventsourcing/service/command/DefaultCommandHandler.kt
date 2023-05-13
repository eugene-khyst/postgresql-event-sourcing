package com.example.eventsourcing.service.command

import com.example.eventsourcing.domain.Aggregate
import com.example.eventsourcing.domain.command.Command

class DefaultCommandHandler : CommandHandler<Command> {
    override fun handle(aggregate: Aggregate, command: Command) {
        aggregate.process(command)
    }

    override val commandType = Command::class.java
}
