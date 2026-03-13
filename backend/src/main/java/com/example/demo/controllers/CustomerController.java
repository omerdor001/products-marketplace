package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;
import com.fasterxml.jackson.annotation.JsonView;
import com.example.demo.domain.Views;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {         
    private final ProductService productService;
    private final PurchaseService purchaseService;

    public CustomerController(ProductService productService, PurchaseService purchaseService) {
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    @JsonView(Views.Public.class)
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAvailableProducts() {     
        try {
            List<Product> products = productService.getAvailableProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
