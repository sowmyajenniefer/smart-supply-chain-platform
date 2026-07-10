package com.portfolio.supplychain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.supplychain.config.KafkaTopicConfig;
import com.portfolio.supplychain.event.PaymentCompletedEvent;
import com.portfolio.supplychain.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(
            topics = KafkaTopicConfig.PAYMENT_COMPLETED_TOPIC,
            groupId = "order-payment-completed-group"
    )
    public void consumePaymentCompletedEvent(String payload) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(payload, PaymentCompletedEvent.class);

            log.info("Received PAYMENT_COMPLETED event for orderNumber={}, paymentReference={}",
                    event.getOrderNumber(),
                    event.getPaymentReference());

            orderService.handlePaymentCompleted(event);

        } catch (Exception exception) {
            log.error("Failed to process PAYMENT_COMPLETED event. payload={}", payload, exception);
        }
    }

    @KafkaListener(
            topics = KafkaTopicConfig.PAYMENT_FAILED_TOPIC,
            groupId = "order-payment-failed-group"
    )
    public void consumePaymentFailedEvent(String payload) {
        try {
            PaymentFailedEvent event = objectMapper.readValue(payload, PaymentFailedEvent.class);

            log.info("Received PAYMENT_FAILED event for orderNumber={}, reason={}",
                    event.getOrderNumber(),
                    event.getFailureReason());

            orderService.handlePaymentFailed(event);

        } catch (Exception exception) {
            log.error("Failed to process PAYMENT_FAILED event. payload={}", payload, exception);
        }
    }
}