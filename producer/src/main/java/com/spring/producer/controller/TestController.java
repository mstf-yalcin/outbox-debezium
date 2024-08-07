package com.spring.producer.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.producer.model.Outbox;
import com.spring.producer.model.User;
import com.spring.producer.repository.OutboxRepository;
import com.spring.producer.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class TestController {

    private final UserRepository userRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    public TestController(UserRepository userRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/produce")
    public ResponseEntity<String> postMessage(@RequestBody User data) {
        try {
            userRepository.save(data);

            String payload = objectMapper.writeValueAsString(data);
            Outbox outbox = Outbox.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("user-topic.0")
                    .aggregateId(data.getId())
                    .type("UserCreated")
                    .payload(payload)
                    .build();

            outboxRepository.save(outbox);

            return ResponseEntity.ok("Message sent successfully: " + data);

        } catch (JsonProcessingException e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }


}
