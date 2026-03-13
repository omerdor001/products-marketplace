package com.example.demo.facades;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.repositories.Products_DB_Repository;
import com.example.demo.repositories.Products_Memory_Repository;

@Component
public class ProductFacade {
    private final Products_DB_Repository dbRepository;
    private final Products_Memory_Repository memoryRepository;

    public ProductFacade(Products_DB_Repository dbRepository, Products_Memory_Repository memoryRepository) {
        this.dbRepository = dbRepository;
        this.memoryRepository = memoryRepository;
    }

    public void addCoupon(String username, String name, String description, String imageUrl, double costPrice,
            double marginPercentage, Coupon.ValueType valueType, String value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon name cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon description cannot be null or empty");
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Image URL must start with http:// or https://");
        }
        if (costPrice < 0) {
            throw new IllegalArgumentException("Cost price cannot be negative");
        }
        if (marginPercentage < 0 || marginPercentage > 100) {
            throw new IllegalArgumentException("Margin percentage must be between 0 and 100");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon value cannot be null or empty");
        }
        UUID productId = dbRepository.addCoupon(username, name, description, imageUrl, costPrice, marginPercentage,
                valueType, value);
        memoryRepository.addCoupon(productId, username, name, description, imageUrl, costPrice, marginPercentage,
                valueType, value);

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

    public List<Product> getAvailableProducts() {
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

    public void updateCouponCostPrice(String username, UUID productId, double costPrice) {
        dbRepository.updateCouponCostPrice(username, productId, costPrice);
        memoryRepository.updateCouponCostPrice(username, productId, costPrice);
    }

    public void updateCouponMarginPercentage(String username, UUID productId, double marginPercentage) {
        dbRepository.updateCouponMarginPercentage(username, productId, marginPercentage);
        memoryRepository.updateCouponMarginPercentage(username, productId, marginPercentage);
    }

    public void updateCouponValue(String username, UUID productId, Coupon.ValueType valueType, String value) {
        dbRepository.updateCouponValue(username, productId, valueType, value);
        memoryRepository.updateCouponValue(username, productId, valueType, value);
    }

    public void updateImageURL(String username, UUID productId, String imageUrl) {
        dbRepository.updateImageURL(username, productId, imageUrl);
        memoryRepository.updateImageURL(username, productId, imageUrl);
    }

    public String getValueType(UUID productId) {
        return memoryRepository.getValueType(productId);
    }

    public void markAsSold(UUID productId) {
        dbRepository.markAsSold(productId);
        memoryRepository.markAsSold(productId);
    }

    public void removeProduct(String username, UUID productId) {
        dbRepository.removeProduct(username, productId);
        memoryRepository.removeProduct(username, productId);
    }

    public String purchaseProductByCustomer(UUID productId) {
        try {
            String value = memoryRepository.purchaseProductByCustomer(productId);
            dbRepository.purchaseProductByCustomer(productId); 
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Purchase failed. Transaction cancelled.", e);
        }
    }

    public double purchaseProductByReseller(UUID productId, double resellerPrice) {
        try {
            double price = memoryRepository.purchaseProductByReseller(productId, resellerPrice);
            dbRepository.purchaseProductByReseller(productId, resellerPrice); 
            return price;
        } catch (Exception e) {
            throw new RuntimeException("Purchase failed. Transaction cancelled.", e);
        }
    }

}
