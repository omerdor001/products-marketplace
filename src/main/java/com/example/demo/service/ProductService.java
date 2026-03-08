package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.repositories.Products_Memory_Repository;

public class ProductService {
    private static ProductService instance;
    private Products_Memory_Repository productsRepository;

    private ProductService() {
        productsRepository = Products_Memory_Repository.getInstance();
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    //For testing purposes only
    public static void resetInstance() {
        Products_Memory_Repository.resetInstance();
        instance = null;
    }

    public void addCoupon(String name, String description, String imageUrl, double costPrice, double marginPercentage, Coupon.ValueType valueType, String value)  {
        productsRepository.addCoupon(name, description, imageUrl, costPrice, marginPercentage, valueType, value);
    }

    public void removeProduct(UUID productId) {
        productsRepository.removeProduct(productId);
    }

    public List<Product> getAllProducts() {
       return productsRepository.getAllProducts();
    }

    public Product getProductById(UUID productId) {
        return productsRepository.getProductById(productId);
    }

    public List<Product> getAvailableProducts() {
        return productsRepository.getAvailableProducts();
    }

    public void updateCouponCostPrice(UUID productId, double costPrice) {
        productsRepository.updateCouponCostPrice(productId, costPrice);
    }

    public void updateCouponMarginPercentage(UUID productId, double marginPercentage) {
        productsRepository.updateCouponMarginPercentage(productId, marginPercentage);
    }

    public void updateCouponValue(UUID productId, Coupon.ValueType valueType, String value) {
        productsRepository.updateCouponValue(productId, valueType, value);
    }

    public void updateImageURL(UUID productId, String imageUrl) {
        productsRepository.updateImageURL(productId, imageUrl);
    }   

    public void markAsSold(UUID productId) {
        productsRepository.markAsSold(productId);
    }
}


