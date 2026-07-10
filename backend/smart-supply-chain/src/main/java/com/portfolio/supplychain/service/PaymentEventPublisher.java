package com.portfolio.supplychain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.supplychain.config.KafkaTopicConfig;
import com.portfolio.supplychain.event.PaymentCompletedEvent;
import com.portfolio.supplychain.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC,
                    event.getOrderNumber(),
                    payload
            ).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to publish PAYMENT_COMPLETED event for orderNumber={}",
                            event.getOrderNumber(), exception);
                } else {
                    log.info("Published PAYMENT_COMPLETED event for orderNumber={} to topic={}",
                            event.getOrderNumber(),
                            KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC);
                }
            });

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize payment completed event", exception);
        }
    }

    public void publishPaymentFailedEvent(PaymentFailedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    KafkaTopicConfig.PAYMENT_FAILED_TOPIC,
                    event.getOrderNumber(),
                    payload
            ).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to publish PAYMENT_FAILED event for orderNumber={}",
                            event.getOrderNumber(), exception);
                } else {
                    log.info("Published PAYMENT_FAILED event for orderNumber={} to topic={}",
                            event.getOrderNumber(),
                            KafkaTopicConfig.PAYMENT_FAILED_TOPIC);
                }
            });

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize payment failed event", exception);
        }
    }
}