package com.example.eventsourcing.controller

import com.example.eventsourcing.error.ErrorMessage
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.InvocationTargetException

object ErrorHandler : (Throwable) -> Response {
    private val errorMessageLens = Body.auto<ErrorMessage>().toLens()

    override fun invoke(e: Throwable): Response {
        return when (e) {
            is UnsupportedOperationException -> {
                when (val cause = e.cause) {
                    is InvocationTargetException -> Response(Status.BAD_REQUEST)
                        .with(errorMessageLens of ErrorMessage((cause.targetException.message ?: "").format(e)))

                    else -> {
                        Response(Status.BAD_REQUEST)
                            .with(errorMessageLens of ErrorMessage((e.message ?: "").format(e)))
                    }
                }
            }

            else -> {
                if (e !is Exception) throw e

                val stackTraceAsString = StringWriter().apply {
                    e.printStackTrace(PrintWriter(this))
                }.toString()

                Response(Status.INTERNAL_SERVER_ERROR).body(stackTraceAsString)
            }
        }
    }
}
