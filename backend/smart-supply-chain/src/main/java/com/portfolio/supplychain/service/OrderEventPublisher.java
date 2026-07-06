package com.portfolio.supplychain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.supplychain.config.KafkaTopicConfig;
import com.portfolio.supplychain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    KafkaTopicConfig.ORDER_CREATED_TOPIC,
                    event.getOrderNumber(),
                    payload
            ).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to publish ORDER_CREATED event for orderNumber={}",
                            event.getOrderNumber(), exception);
                } else {
                    log.info("Published ORDER_CREATED event for orderNumber={} to topic={}",
                            event.getOrderNumber(),
                            KafkaTopicConfig.ORDER_CREATED_TOPIC);
                }
            });

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize order created event", exception);
        }
    }
}