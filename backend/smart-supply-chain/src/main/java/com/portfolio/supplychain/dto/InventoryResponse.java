package com.portfolio.supplychain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long id;

    private Long productId;
    private String sku;
    private String productName;

    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderLevel;
    private String warehouseLocation;

    private Boolean lowStock;

    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}