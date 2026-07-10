package com.portfolio.supplychain.entity;

public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    CONFIRMED,
    CANCELLED
}