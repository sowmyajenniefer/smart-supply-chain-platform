package com.portfolio.supplychain.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {

    private Long orderId;
    private String orderNumber;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String failureReason;
    private LocalDateTime failedAt;
}