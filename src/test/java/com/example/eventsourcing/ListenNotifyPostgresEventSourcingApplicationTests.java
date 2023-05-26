package com.example.eventsourcing;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "event-sourcing.subscriptions=postgres-channel")
class ListenNotifyPostgresEventSourcingApplicationTests extends PostgresEventSourcingApplicationTests {
}
