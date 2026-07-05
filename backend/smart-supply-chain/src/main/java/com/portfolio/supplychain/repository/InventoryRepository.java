package com.portfolio.supplychain.repository;

import com.portfolio.supplychain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct_Id(Long productId);

    boolean existsByProduct_Id(Long productId);
}