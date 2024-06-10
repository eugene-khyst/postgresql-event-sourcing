package com.example.kotlin.functional

import com.example.eventsourcing.config.Kafka.TOPIC_ORDER_EVENTS
import com.example.kotlin.functional.TestEnvironment.kafka
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)

class PostgreSqlEventSourcingApplicationTests {
    private val script = OrderTestScript(TestEnvironment.newApp(TOPIC_ORDER_EVENTS), kafka.bootstrapServers, TOPIC_ORDER_EVENTS)

    @Test
    fun `can create new order`(){
        script.placeNewOrder()
    }

    @Test
    fun orderTestScript() {
        OrderTestScript(TestEnvironment.newApp(TOPIC_ORDER_EVENTS), kafka.bootstrapServers, TOPIC_ORDER_EVENTS).execute()
    }
}
