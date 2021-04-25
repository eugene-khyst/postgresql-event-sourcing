package com.example.eventsourcing.eventstore.repository;

import com.example.eventsourcing.eventstore.domain.readmodel.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {}
