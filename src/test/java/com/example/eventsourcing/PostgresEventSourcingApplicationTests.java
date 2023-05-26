package com.example.eventsourcing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisabledIfEnvironmentVariable(named = "E2E_TESTING", matches = "true")
abstract class PostgresEventSourcingApplicationTests extends AbstractContainerBaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void orderTestScript() throws Exception {
        new OrderTestScript(restTemplate, KAFKA.getBootstrapServers()).execute();
    }
}
