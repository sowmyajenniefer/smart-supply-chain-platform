package com.portfolio.supplychain.controller;

import com.portfolio.supplychain.dto.ProductRequest;
import com.portfolio.supplychain.dto.ProductResponse;
import com.portfolio.supplychain.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "APIs for managing product catalog")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create a new product", description = "Creates a new product in the supply chain catalog")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all products", description = "Retrieves all products from the product catalog")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a single product using its product ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Update product", description = "Updates product details by product ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete product", description = "Deletes a product by product ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}