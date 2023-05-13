package com.example.kotlin.e2e

import com.example.kotlin.functional.OrderTestScript
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder

class PostgreSqlEventSourcingApplicationE2ETests {
    @Test
    fun orderTestScript() {
        val restTemplate = RestTemplateBuilder().rootUri(ROOT_URI)
        OrderTestScript(TestRestTemplate(restTemplate), KAFKA_BROKERS).execute()
    }

    companion object {
        private const val ROOT_URI = "http://localhost:8080"
        private const val KAFKA_BROKERS = "localhost:9092"
    }
}
