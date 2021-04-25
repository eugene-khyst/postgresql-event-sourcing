package com.example.eventsourcing.eventstore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("outbox")
@Data
public class OutboxProperties {

  private String subscriptionName;
}
