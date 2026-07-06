package com.portfolio.supplychain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.supplychain.config.KafkaTopicConfig;
import com.portfolio.supplychain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopicConfig.ORDER_CREATED_TOPIC)
    public void consumeOrderCreatedEvent(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

            log.info("Received ORDER_CREATED event. orderNumber={}, customerEmail={}, totalAmount={}",
                    event.getOrderNumber(),
                    event.getCustomerEmail(),
                    event.getTotalAmount());

            log.info("Simulated downstream workflow triggered for orderNumber={}",
                    event.getOrderNumber());

        } catch (Exception exception) {
            log.error("Failed to process ORDER_CREATED event. payload={}", payload, exception);
        }
    }
}