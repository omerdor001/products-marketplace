package com.example.demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.domain.Views;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/coupon")
    public void addCoupon(@RequestParam String username,@RequestParam String name, @RequestParam String description, @RequestParam String imageUrl, @RequestParam double costPrice, @RequestParam double marginPercentage,
            @RequestParam Coupon.ValueType valueType, @RequestParam String value) {
        productService.addCoupon(username, name, description, imageUrl,
                costPrice, marginPercentage, valueType, value);
    }


    @DeleteMapping("/{productId}")
    public void removeProduct(@RequestParam String username, @PathVariable UUID productId) {
        productService.removeProduct(username, productId);
    }

    @JsonView(Views.Public.class)
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @JsonView(Views.Admin.class)
    @GetMapping("/availables")
    public List<Product> getAvailableProducts() {
        return productService.getAvailableProducts();
    }

    @PutMapping("/{productId}/cost-price")
    public void updateCouponCostPrice(@RequestParam String username, @PathVariable UUID productId, @RequestParam double costPrice) {
        productService.updateCouponCostPrice(username, productId, costPrice);
    }

    @PutMapping("/{productId}/margin")
    public void updateCouponMarginPercentage(@RequestParam String username, @PathVariable UUID productId, @RequestParam double marginPercentage) {
        productService.updateCouponMarginPercentage(username, productId, marginPercentage);
    }

    @PutMapping("/{productId}/value")
    public void updateCouponValue(@RequestParam String username, @PathVariable UUID productId, @RequestParam Coupon.ValueType valueType, @RequestParam String value) {
        productService.updateCouponValue(username, productId, valueType, value);
    }

    @PutMapping("/{productId}/image-url")
    public void updateImageURL(@RequestParam String username, @PathVariable UUID productId, @RequestParam String imageUrl) {
        productService.updateImageURL(username, productId, imageUrl);
    }
}