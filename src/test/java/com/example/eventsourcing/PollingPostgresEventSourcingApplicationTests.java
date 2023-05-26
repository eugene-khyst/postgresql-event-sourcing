package com.example.eventsourcing;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "event-sourcing.subscriptions=polling")
class PollingPostgresEventSourcingApplicationTests extends PostgresEventSourcingApplicationTests {
}
