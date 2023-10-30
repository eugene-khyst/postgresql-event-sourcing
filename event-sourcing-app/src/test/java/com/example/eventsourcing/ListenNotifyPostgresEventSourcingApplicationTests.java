package com.example.eventsourcing;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.test.context.TestPropertySource;

import static com.example.eventsourcing.PostgresEventSourcingApplicationE2ETests.E2E_TESTING_ENV_VAR;

@TestPropertySource(properties = "event-sourcing.subscriptions=postgres-channel")
@DisabledIfEnvironmentVariable(named = E2E_TESTING_ENV_VAR, matches = "true")
class ListenNotifyPostgresEventSourcingApplicationTests extends PostgresEventSourcingApplicationTests {
}
