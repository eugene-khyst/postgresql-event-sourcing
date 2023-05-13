package com.example.eventsourcing.service.command

import com.example.eventsourcing.domain.Aggregate
import com.example.eventsourcing.domain.command.Command
import com.example.eventsourcing.domain.command.PlaceOrderCommand

class PlaceOrderCommandHandler : CommandHandler<PlaceOrderCommand> {
    override fun handle(aggregate: Aggregate, command: Command) {
        // Add additional business logic here.
        aggregate.process(command)
        // Also, add additional business logic here.
        // Read other aggregates using AggregateStore.
    }

    override val commandType = PlaceOrderCommand::class.java
}
