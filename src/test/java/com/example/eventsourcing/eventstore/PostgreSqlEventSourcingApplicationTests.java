package com.example.eventsourcing.eventstore;

import com.example.eventsourcing.eventstore.config.KafkaTopicsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    topics = {KafkaTopicsConfig.TOPIC_ORDER_INTEGRATION_EVENTS})
class PostgreSqlEventSourcingApplicationTests {

  @Test
  void contextLoads() {}
}
