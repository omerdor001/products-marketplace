package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.domain.Views;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {    

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @JsonView(Views.Public.class)
    @PostMapping("/coupon")
    public ResponseEntity<String> addCoupon(@RequestParam String username, @RequestParam String name, @RequestParam String description, @RequestParam String imageUrl,
            @RequestParam double costPrice, @RequestParam double marginPercentage, @RequestParam Coupon.ValueType valueType, @RequestParam String value) {
        try {
            productService.addCoupon(username, name, description, imageUrl,
                    costPrice, marginPercentage, valueType, value);
            return ResponseEntity.ok("Coupon added successfully");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeProduct(@RequestParam String username,
            @PathVariable UUID productId) {
        try {
            productService.removeProduct(username, productId);
            return ResponseEntity.ok("Product removed successfully");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @JsonView(Views.Admin.class)
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @JsonView(Views.Public.class)
    @GetMapping("/availables")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        try {
            List<Product> products = productService.getAvailableProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @JsonView(Views.Public.class)
    @PutMapping("/{productId}/cost-price")
    public ResponseEntity<String> updateCouponCostPrice(@RequestParam String username, @PathVariable UUID productId, @RequestParam double costPrice) {
        try {
            productService.updateCouponCostPrice(username, productId, costPrice);
            return ResponseEntity.ok("Cost price updated");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{productId}/margin")
    public ResponseEntity<String> updateCouponMarginPercentage(@RequestParam String username, @PathVariable UUID productId, @RequestParam double marginPercentage) {
        try {
            productService.updateCouponMarginPercentage(username, productId, marginPercentage);
            return ResponseEntity.ok("Margin percentage updated");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{productId}/value")
    public ResponseEntity<String> updateCouponValue(@RequestParam String username, @PathVariable UUID productId, @RequestParam Coupon.ValueType valueType, @RequestParam String value) {
        try {
            productService.updateCouponValue(username, productId, valueType, value);
            return ResponseEntity.ok("Coupon value updated");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @JsonView(Views.Public.class)
    @PutMapping("/{productId}/image-url")
    public ResponseEntity<String> updateImageURL(@RequestParam String username,
            @PathVariable UUID productId,
            @RequestParam String imageUrl) {
        try {
            productService.updateImageURL(username, productId, imageUrl);
            return ResponseEntity.ok("Image URL updated");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}