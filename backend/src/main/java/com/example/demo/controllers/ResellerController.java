package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;

@RestController
@RequestMapping("/api/v1/")
public class ResellerController {
    private final ProductService productService;
    private final PurchaseService purchaseService;

    public ResellerController(ProductService productService, PurchaseService purchaseService) {
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("products")
    public ResponseEntity<?> getAvailableProducts(@RequestParam(required = false) String token) {
        try {
            List<Product> products = productService.getAvailableProducts(token);
            return ResponseEntity.ok(products);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error_code", "UNAUTHORIZED",
                            "message", "Unauthorized purchase attempt"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error_code", "INTERNAL_ERROR",
                            "message", e.getMessage()));
        }
    }

    @GetMapping("products/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId, @RequestParam(required = false) String token) {
        try {
            Product product = productService.getProductById(productId, token);
            return ResponseEntity.ok(product);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error_code", "PRODUCT_NOT_FOUND",
                            "message", "Product not found"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error_code", "UNAUTHORIZED",
                            "message", "Unauthorized purchase attempt"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error_code", "INTERNAL_ERROR",
                            "message", e.getMessage()));
        }
    }

    @GetMapping("{productId}/purchase/{resellerPrice}/{token}")
    public ResponseEntity<?> purchaseProduct(
            @PathVariable UUID productId,
            @PathVariable Double resellerPrice,
            @PathVariable String token) {
        try {
            double price = purchaseService.purchaseProductByReseller(productId, resellerPrice, token);
            return ResponseEntity.ok(price);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error_code", "PRODUCT_NOT_FOUND",
                            "message", "Product not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error_code", "PRODUCT_ALREADY_SOLD",
                            "message", "Product has already been sold"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error_code", "RESELLER_PRICE_TOO_LOW",
                            "message", "Reseller price is too low"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error_code", "UNAUTHORIZED",
                            "message", "Unauthorized purchase attempt"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error_code", "INTERNAL_ERROR",
                            "message", e.getMessage()));
        }
    }

}
