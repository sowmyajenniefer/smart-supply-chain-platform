package com.portfolio.supplychain.service;

import com.portfolio.supplychain.dto.InventoryRequest;
import com.portfolio.supplychain.dto.InventoryResponse;
import com.portfolio.supplychain.dto.InventoryUpdateRequest;
import com.portfolio.supplychain.dto.StockAdjustmentRequest;
import com.portfolio.supplychain.entity.Inventory;
import com.portfolio.supplychain.entity.Product;
import com.portfolio.supplychain.exception.ResourceNotFoundException;
import com.portfolio.supplychain.repository.InventoryRepository;
import com.portfolio.supplychain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()
                ));

        if (inventoryRepository.existsByProduct_Id(request.getProductId())) {
            throw new IllegalArgumentException(
                    "Inventory already exists for product id: " + request.getProductId()
            );
        }

        Inventory inventory = Inventory.builder()
                .product(product)
                .availableQuantity(request.getAvailableQuantity())
                .reservedQuantity(0)
                .reorderLevel(request.getReorderLevel())
                .warehouseLocation(request.getWarehouseLocation())
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);

        return mapToResponse(savedInventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = findInventoryByProductId(productId);
        return mapToResponse(inventory);
    }

    @Transactional
    public InventoryResponse updateInventory(Long productId, InventoryUpdateRequest request) {
        Inventory inventory = findInventoryByProductId(productId);

        inventory.setAvailableQuantity(request.getAvailableQuantity());
        inventory.setReorderLevel(request.getReorderLevel());
        inventory.setWarehouseLocation(request.getWarehouseLocation());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return mapToResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse reserveStock(Long productId, StockAdjustmentRequest request) {
        Inventory inventory = findInventoryByProductId(productId);

        int requestedQuantity = request.getQuantity();

        if (inventory.getAvailableQuantity() < requestedQuantity) {
            throw new IllegalArgumentException(
                    "Insufficient inventory. Available quantity: "
                            + inventory.getAvailableQuantity()
                            + ", requested quantity: "
                            + requestedQuantity
            );
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - requestedQuantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + requestedQuantity);

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return mapToResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse releaseStock(Long productId, StockAdjustmentRequest request) {
        Inventory inventory = findInventoryByProductId(productId);

        int releaseQuantity = request.getQuantity();

        if (inventory.getReservedQuantity() < releaseQuantity) {
            throw new IllegalArgumentException(
                    "Cannot release more than reserved quantity. Reserved quantity: "
                            + inventory.getReservedQuantity()
                            + ", release quantity: "
                            + releaseQuantity
            );
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - releaseQuantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + releaseQuantity);

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return mapToResponse(updatedInventory);
    }

    private Inventory findInventoryByProductId(Long productId) {
        return inventoryRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product id: " + productId
                ));
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        Product product = inventory.getProduct();

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .reorderLevel(inventory.getReorderLevel())
                .warehouseLocation(inventory.getWarehouseLocation())
                .lowStock(isLowStock(inventory))
                .version(inventory.getVersion())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private boolean isLowStock(Inventory inventory) {
        return inventory.getAvailableQuantity() <= inventory.getReorderLevel();
    }
}