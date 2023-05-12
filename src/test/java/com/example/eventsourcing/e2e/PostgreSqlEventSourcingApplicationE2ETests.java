package com.example.eventsourcing.e2e;

import com.example.eventsourcing.functional.OrderTestScript;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

@EnabledIfEnvironmentVariable(named = "E2E_TESTING", matches = "true")
class PostgreSqlEventSourcingApplicationE2ETests {

    private static final String ROOT_URI = "http://localhost:8080";
    private static final String KAFKA_BROKERS = "localhost:9092";

    @Test
    void orderTestScript() {
        var restTemplate = new RestTemplateBuilder().rootUri(ROOT_URI);
        new OrderTestScript(new TestRestTemplate(restTemplate), KAFKA_BROKERS).execute();
    }
}
