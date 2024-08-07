package com.spring.producer.consumer;

import com.spring.producer.model.User;
import com.spring.producer.repository.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class Consumer {
    private final OutboxRepository outboxRepository;

    public Consumer(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    @KafkaListener(topics = "user-topic.0.outbox-delete", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload User message, @Header(KafkaHeaders.RECEIVED_KEY) String outboxId,
                        Acknowledgment acknowledgment) {

        log.info("Received message producer-group-1: {}", message);

//        outboxRepository.deleteByAggregateId(message.getId());
        outboxRepository.deleteById(UUID.fromString(outboxId));
        acknowledgment.acknowledge();
    }
}
