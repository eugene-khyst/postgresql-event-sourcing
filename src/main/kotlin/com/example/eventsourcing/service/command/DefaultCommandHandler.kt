package com.example.eventsourcing.service.command

import com.example.eventsourcing.domain.Aggregate
import com.example.eventsourcing.domain.command.Command
import org.slf4j.LoggerFactory

class DefaultCommandHandler : CommandHandler<Command> {
    override fun handle(aggregate: Aggregate?, command: Command?) {
        aggregate!!.process(command!!)
    }

    override val commandType = Command::class.java

    companion object {
        private val log = LoggerFactory.getLogger(DefaultCommandHandler::class.java)
    }
}
