package com.example.eventsourcing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("eventsourcing.postgresql")
@ComponentScan
@EntityScan
@EnableJpaRepositories
@EnableScheduling
@EnableAsync
public class PostgresEventSourcingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgresEventSourcingApplication.class, args);
    }
}
