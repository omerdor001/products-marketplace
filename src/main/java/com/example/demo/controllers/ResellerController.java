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
@RequestMapping("/api/v1/")
public class ResellerController {
    private final ProductService productService;
    private final PurchaseService purchaseService;
    
    public ResellerController(ProductService productService, PurchaseService purchaseService) {
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("products")
    public List<Product> getAvailableProducts() {
        return productService.getAvailableProducts();
    }

    @GetMapping("products/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }

    @GetMapping("/{productId}/purchase")
    public double purchaseProduct(
            @PathVariable UUID productId,@PathVariable Double resellerPrice,@PathVariable String token) {
        return purchaseService.purchaseProductByReseller(productId,resellerPrice, token);
    }




}
