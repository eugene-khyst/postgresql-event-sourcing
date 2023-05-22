package com.example.kotlin.functional

import com.example.kotlin.functional.TestEnvironment.kafka
import org.junit.jupiter.api.Test

class PostgreSqlEventSourcingApplicationTests {

    @Test
    fun orderTestScript() {
        OrderTestScript(TestEnvironment.appHandler, kafka.bootstrapServers).execute()
    }
}
