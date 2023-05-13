package com.example.eventsourcing.service.command

import com.example.eventsourcing.domain.Aggregate
import com.example.eventsourcing.domain.command.Command

interface CommandHandler<T : Command?> {
    fun handle(aggregate: Aggregate?, command: Command?)
    val commandType: Class<T>
}
