package com.example.kotlin.functional

import com.example.kotlin.functional.TestEnvironment.KAFKA
import org.http4k.client.ApacheClient
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.junit.jupiter.api.Test

class PostgreSqlEventSourcingApplicationTests {

    @Test
    fun orderTestScript() {
        val httpHandler = ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:8080"))
            .then(ApacheClient())
        OrderTestScript(httpHandler, KAFKA.bootstrapServers).execute()
    }
}
