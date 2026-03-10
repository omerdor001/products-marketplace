package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;

@RestController
@RequestMapping("/api/v1/")
public class ResellerController {    //change controller tests 

    private final ProductService productService;
    private final PurchaseService purchaseService;

    public ResellerController(ProductService productService, PurchaseService purchaseService) {
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Invalid token");
        }
        return authHeader.replace("Bearer ", "");
    }

    @GetMapping("products")
    public ResponseEntity<?> getAvailableProducts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error_code", "UNAUTHORIZED",
                                "message", "Missing authorization header"));
            }
            String token = extractToken(authHeader);
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
    public ResponseEntity<?> getProductById(
            @PathVariable UUID productId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error_code", "UNAUTHORIZED",
                                "message", "Missing authorization header"));
            }
            String token = extractToken(authHeader);
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

    @PostMapping("products/{productId}/purchase")
    public ResponseEntity<?> purchaseProduct(
            @PathVariable UUID productId,
            @RequestBody Map<String, Double> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error_code", "UNAUTHORIZED",
                                "message", "Missing authorization header"));
            }
            String token = extractToken(authHeader);
            Double resellerPrice = request.get("reseller_price");
            Map<String, Object> result = Map.of(
                    "product_id", productId,
                    "final_price", resellerPrice,
                    "value_type", "STRING",
                    "value", purchaseService.purchaseProductByReseller(productId, resellerPrice, token));
            return ResponseEntity.ok(result);
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