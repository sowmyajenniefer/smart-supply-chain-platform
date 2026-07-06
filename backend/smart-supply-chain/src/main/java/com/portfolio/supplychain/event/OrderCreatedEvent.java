package com.portfolio.supplychain.event;

import com.portfolio.supplychain.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private Long orderId;
    private String orderNumber;

    private String customerName;
    private String customerEmail;

    private OrderStatus status;
    private BigDecimal totalAmount;

    private List<OrderItemEvent> items;

    private LocalDateTime createdAt;
}