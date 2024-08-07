package com.spring.producer.repository;

import com.spring.producer.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

    void deleteByAggregateId(String aggregateId);
}
