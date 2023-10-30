package com.example.eventsourcing.repository;

import com.example.eventsourcing.projection.OrderProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProjectionRepository extends JpaRepository<OrderProjection, UUID> {
}
