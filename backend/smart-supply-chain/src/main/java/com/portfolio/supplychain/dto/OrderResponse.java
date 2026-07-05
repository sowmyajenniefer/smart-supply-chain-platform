package com.portfolio.supplychain.dto;

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
public class OrderResponse {

    private Long id;
    private String orderNumber;

    private String customerName;
    private String customerEmail;

    private OrderStatus status;
    private BigDecimal totalAmount;

    private List<OrderItemResponse> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}