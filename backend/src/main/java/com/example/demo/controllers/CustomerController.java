package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;

@RestController
@RequestMapping("/customer")
public class CustomerController {         
    private final ProductService productService;
    private final PurchaseService purchaseService;

    public CustomerController(ProductService productService, PurchaseService purchaseService) {
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAvailableProducts() {     
        try {
            List<Product> products = productService.getAvailableProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/purchase/{productId}")
    public ResponseEntity<String> purchaseProductByCustomer(@PathVariable UUID productId) {
        try {
            String result = purchaseService.purchaseProductByCustomer(productId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
