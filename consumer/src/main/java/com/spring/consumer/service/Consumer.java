package com.spring.consumer.service;


import com.spring.consumer.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Consumer {

    private final KafkaTemplate kafkaTemplate;

    public Consumer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "user-topic.0", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload User message, @Header("id") String outboxId, Acknowledgment acknowledgment) {

        log.info("Received message consumer-group-1: {}", message);

//        process();
        acknowledgment.acknowledge();

        Message builder = MessageBuilder.withPayload(message)
                .setHeader(KafkaHeaders.KEY, outboxId)
                .setHeader(KafkaHeaders.TOPIC, "user-topic.0.outbox-delete")
                .build();

        kafkaTemplate.send(builder);

    }

    public void process() {
        log.info("error processing message");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Error");
    }

    @KafkaListener(topics = "test-topic.0.error", groupId = "${spring.application.name}-consumer-group-1")
    public void consumeRetry(@Payload User message, Acknowledgment acknowledgment) {
        log.info("retry consumer-group-1: {}", message);
//        process();
        acknowledgment.acknowledge();
    }


}
