package com.example.eventsourcing;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.test.context.TestPropertySource;

import static com.example.eventsourcing.PostgresEventSourcingApplicationE2ETests.E2E_TESTING_ENV_VAR;

@TestPropertySource(properties = "event-sourcing.subscriptions=polling")
@DisabledIfEnvironmentVariable(named = E2E_TESTING_ENV_VAR, matches = "true")
class PollingPostgresEventSourcingApplicationTests extends PostgresEventSourcingApplicationTests {
}
