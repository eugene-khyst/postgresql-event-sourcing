package com.example.kotlin.functional

import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder

internal class PostgreSqlEventSourcingApplicationTests : AbstractContainerBaseTest() {

    @Test
    fun orderTestScript() {
        val restTemplateBuilder = RestTemplateBuilder().rootUri("http://localhost:8080/")
        val testRestTemplate = TestRestTemplate(restTemplateBuilder)
        OrderTestScript(testRestTemplate, KAFKA!!.bootstrapServers).execute()
    }
}
