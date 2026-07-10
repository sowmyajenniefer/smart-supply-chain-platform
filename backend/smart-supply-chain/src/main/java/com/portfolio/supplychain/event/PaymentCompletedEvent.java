package com.portfolio.supplychain.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {

    private Long orderId;
    private String orderNumber;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String paymentReference;
    private LocalDateTime paidAt;
}