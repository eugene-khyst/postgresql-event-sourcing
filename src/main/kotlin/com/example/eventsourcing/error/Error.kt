package com.example.eventsourcing.error

open class Error(message: String, vararg args: Any?) : RuntimeException(message.formatted(*args))
