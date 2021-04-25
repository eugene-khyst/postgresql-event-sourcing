package com.example.eventsourcing.eventstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PostgreSqlEventSourcingApplication {

  public static void main(String[] args) {
    SpringApplication.run(PostgreSqlEventSourcingApplication.class, args);
  }
}
