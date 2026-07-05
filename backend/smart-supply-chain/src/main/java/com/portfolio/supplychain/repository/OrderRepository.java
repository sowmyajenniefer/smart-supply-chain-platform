package com.portfolio.supplychain.repository;

import com.portfolio.supplychain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
}