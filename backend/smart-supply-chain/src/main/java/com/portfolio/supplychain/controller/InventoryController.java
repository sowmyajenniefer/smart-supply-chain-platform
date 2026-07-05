package com.portfolio.supplychain.controller;

import com.portfolio.supplychain.dto.InventoryRequest;
import com.portfolio.supplychain.dto.InventoryResponse;
import com.portfolio.supplychain.dto.InventoryUpdateRequest;
import com.portfolio.supplychain.dto.StockAdjustmentRequest;
import com.portfolio.supplychain.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "APIs for managing product inventory and stock reservations")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Create inventory", description = "Creates an inventory record for an existing product")
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all inventory", description = "Retrieves all inventory records")
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        List<InventoryResponse> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Get inventory by product ID", description = "Retrieves inventory details for a specific product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
        InventoryResponse response = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update inventory", description = "Updates inventory details for a specific product")
    @PutMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryUpdateRequest request) {

        InventoryResponse response = inventoryService.updateInventory(productId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reserve stock", description = "Reserves available inventory for a product")
    @PostMapping("/product/{productId}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request) {

        InventoryResponse response = inventoryService.reserveStock(productId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Release reserved stock", description = "Releases previously reserved inventory back to available stock")
    @PostMapping("/product/{productId}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request) {

        InventoryResponse response = inventoryService.releaseStock(productId, request);
        return ResponseEntity.ok(response);
    }
}