package com.portfolio.supplychain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.supplychain.config.KafkaTopicConfig;
import com.portfolio.supplychain.event.OrderCreatedEvent;
import com.portfolio.supplychain.event.PaymentCompletedEvent;
import com.portfolio.supplychain.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSimulationConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    @KafkaListener(
            topics = KafkaTopicConfig.ORDER_CREATED_TOPIC,
            groupId = "payment-service-group"
    )
    public void consumeOrderCreatedEvent(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

            log.info("Payment simulation started for orderNumber={}, totalAmount={}",
                    event.getOrderNumber(),
                    event.getTotalAmount());

            boolean paymentSuccessful = simulatePaymentResult();

            if (paymentSuccessful) {
                PaymentCompletedEvent completedEvent = PaymentCompletedEvent.builder()
                        .orderId(event.getOrderId())
                        .orderNumber(event.getOrderNumber())
                        .customerEmail(event.getCustomerEmail())
                        .totalAmount(event.getTotalAmount())
                        .paymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                        .paidAt(LocalDateTime.now())
                        .build();

                paymentEventPublisher.publishPaymentCompletedEvent(completedEvent);

            } else {
                PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                        .orderId(event.getOrderId())
                        .orderNumber(event.getOrderNumber())
                        .customerEmail(event.getCustomerEmail())
                        .totalAmount(event.getTotalAmount())
                        .failureReason("Simulated payment authorization failure")
                        .failedAt(LocalDateTime.now())
                        .build();

                paymentEventPublisher.publishPaymentFailedEvent(failedEvent);
            }

        } catch (Exception exception) {
            log.error("Failed to process ORDER_CREATED event for payment simulation. payload={}",
                    payload, exception);
        }
    }

    private boolean simulatePaymentResult() {
        return ThreadLocalRandom.current().nextInt(100) < 80;
    }
}