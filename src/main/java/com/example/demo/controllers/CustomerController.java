package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

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
    public List<Product> getAvailableProducts() {
        return productService.getAvailableProducts();
    }

    @GetMapping("/purchase/{productId}")
    public String purchaseProductByCustomer(@PathVariable UUID productId) {
        return purchaseService.purchaseProductByCustomer(productId);
    }








}
