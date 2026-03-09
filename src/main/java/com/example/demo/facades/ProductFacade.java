package com.example.demo.facades;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.repositories.Products_DB_Repository;
import com.example.demo.repositories.Products_Memory_Repository;

public class ProductFacade {
    private final Products_DB_Repository dbRepository;
    private final Products_Memory_Repository memoryRepository;

    public ProductFacade(Products_DB_Repository dbRepository, Products_Memory_Repository memoryRepository) {
        this.dbRepository = dbRepository;
        this.memoryRepository = memoryRepository;
    }

    public void addCoupon(String username, String name, String description, String imageUrl, double costPrice,
            double marginPercentage, Coupon.ValueType valueType, String value) {
        memoryRepository.addCoupon(username, name, description, imageUrl, costPrice, marginPercentage, valueType, value);
        dbRepository.addCoupon(username, name, description, imageUrl, costPrice, marginPercentage, valueType, value);
    }

    public List<Product> getAllProducts() {
        try {
            List<Product> memoryProducts = memoryRepository.getAllProducts();
            if (memoryProducts != null && !memoryProducts.isEmpty()) {
                return memoryProducts;
            }
        } catch (Exception e) {
            System.out.println("Memory repository failed, falling back to DB");
        }
        return dbRepository.getAllProducts();
    }

    public List<Product> getAvailableProducts(){
        try {
            List<Product> memoryProducts = memoryRepository.getAvailableProducts();
            if (memoryProducts != null && !memoryProducts.isEmpty()) {
                return memoryProducts;
            }
        } catch (Exception e) {
            System.out.println("Memory repository failed, falling back to DB");
        }
        return dbRepository.getAvailableProducts();
    }

    public Product getProductById(UUID productId) {
        try {
            Product memoryProduct = memoryRepository.getProductById(productId);
            if (memoryProduct != null) {
                return memoryProduct;
            }
        } catch (Exception e) {
            System.out.println("Memory repository failed, falling back to DB");
        }
        return dbRepository.getProductById(productId);
    }

    public void updateCouponCostPrice(String username,UUID productId, double costPrice) {
        memoryRepository.updateCouponCostPrice(username, productId, costPrice);
        dbRepository.updateCouponCostPrice(username, productId, costPrice);
    }

    public void updateCouponMarginPercentage(String username,UUID productId, double marginPercentage) {
        memoryRepository.updateCouponMarginPercentage(username, productId, marginPercentage);
        dbRepository.updateCouponMarginPercentage(username, productId, marginPercentage);
    }

    public void updateCouponValue(String username,UUID productId, Coupon.ValueType valueType, String value) {
        memoryRepository.updateCouponValue(username, productId, valueType, value);
        dbRepository.updateCouponValue(username, productId, valueType, value);
    }

    public void updateImageURL(String username,UUID productId, String imageUrl) {
        memoryRepository.updateImageURL(username, productId, imageUrl);
        dbRepository.updateImageURL(username, productId, imageUrl);
    }

    public void markAsSold(UUID productId) {
        memoryRepository.markAsSold(productId);
        dbRepository.markAsSold(productId);
    }

    public void removeProduct(String username,UUID productId) {
        memoryRepository.removeProduct(username, productId);
        dbRepository.removeProduct(username, productId);
    }

    public String purchaseProductByCustomer(UUID productId) {
        try {
            return memoryRepository.purchaseProductByCustomer(productId);
        } catch (Exception e) {
            throw new RuntimeException("Purchase failed. Transaction cancelled.", e);
        }
    }

    public double purchaseProductByReseller(UUID productId, double resellerPrice) {
        try {
            return memoryRepository.purchaseProductByReseller(productId, resellerPrice);
        } catch (Exception e) {
            throw new RuntimeException("Purchase failed. Transaction cancelled.", e);
        }
    }

}
