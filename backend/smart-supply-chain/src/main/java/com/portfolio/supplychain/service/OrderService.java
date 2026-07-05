package com.portfolio.supplychain.service;

import com.portfolio.supplychain.dto.OrderItemRequest;
import com.portfolio.supplychain.dto.OrderItemResponse;
import com.portfolio.supplychain.dto.OrderRequest;
import com.portfolio.supplychain.dto.OrderResponse;
import com.portfolio.supplychain.dto.StockAdjustmentRequest;
import com.portfolio.supplychain.entity.Order;
import com.portfolio.supplychain.entity.OrderItem;
import com.portfolio.supplychain.entity.OrderStatus;
import com.portfolio.supplychain.entity.Product;
import com.portfolio.supplychain.exception.ResourceNotFoundException;
import com.portfolio.supplychain.repository.OrderRepository;
import com.portfolio.supplychain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal orderTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()
                    ));

            StockAdjustmentRequest stockAdjustmentRequest = StockAdjustmentRequest.builder()
                    .quantity(itemRequest.getQuantity())
                    .build();

            inventoryService.reserveStock(product.getId(), stockAdjustmentRequest);

            BigDecimal lineTotal = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .lineTotal(lineTotal)
                    .build();

            order.addItem(orderItem);

            orderTotal = orderTotal.add(lineTotal);
        }

        order.setTotalAmount(orderTotal);
        order.setStatus(OrderStatus.INVENTORY_RESERVED);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerName(String customerName) {
        return orderRepository.findByCustomerNameContainingIgnoreCase(customerName)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrderById(id);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order is already cancelled");
        }

        for (OrderItem item : order.getItems()) {
            StockAdjustmentRequest stockAdjustmentRequest = StockAdjustmentRequest.builder()
                    .quantity(item.getQuantity())
                    .build();

            inventoryService.releaseStock(item.getProduct().getId(), stockAdjustmentRequest);
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder = orderRepository.save(order);

        return mapToResponse(cancelledOrder);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(this::mapItemToResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        Product product = item.getProduct();

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}