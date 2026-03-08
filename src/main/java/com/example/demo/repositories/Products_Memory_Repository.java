package com.example.demo.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;

public class Products_Memory_Repository implements ProductRepository {
    private List<Product> products;

    private static Products_Memory_Repository instance;

    private Products_Memory_Repository() {
        products = new ArrayList<>();
    }

    public static synchronized Products_Memory_Repository getInstance() {
        if (instance == null) {
            instance = new Products_Memory_Repository();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    // ---------------- Add ----------------

    @Override
    public void addCoupon(String name, String description, String imageUrl, double costPrice, double marginPercentage, Coupon.ValueType valueType, String value) {
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
        Coupon coupon = new Coupon(name, description, imageUrl, costPrice, marginPercentage, valueType, value);
        products.add(coupon);
    }

    @Override
    public Product saveProduct(Product product) {
        throw new UnsupportedOperationException("Unimplemented method 'saveProduct'");
    }

     // ---------------- Retrieve ----------------

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public Product getProductById(UUID productId) {
        return products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> getAvailableProducts() {
        List<Product> availableProducts = new ArrayList<>();
        for (Product product : products) {
            if (!product.isSold()) {
                availableProducts.add(product);
            }
        }
        return availableProducts;
    }

    // ---------------- Update ----------------

    @Override
    public void updateCouponCostPrice(UUID productId, double costPrice) {
        if(productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product product = getProductById(productId);
        if(!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        if(costPrice < 0) {
            throw new IllegalArgumentException("Cost price cannot be negative");
        }
        product.setCostPrice(costPrice);
    }

    @Override
    public void updateCouponMarginPercentage(UUID productId, double marginPercentage) {
        if(productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product product = getProductById(productId);
        if(product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if(!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        if(marginPercentage < 0 || marginPercentage > 100) {
            throw new IllegalArgumentException("Margin percentage must be between 0 and 100");
        }
        product.setMarginPercentage(marginPercentage);
    }

    @Override
    public void updateCouponValue(UUID productId, Coupon.ValueType valueType, String value) {
        if(productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product product = getProductById(productId);
        if(product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if(!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        product.setValueType(valueType);
        product.setValue(value);
    }

    @Override
    public void updateImageURL(UUID productId, String imageUrl) {
        if(productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product product = getProductById(productId);
        if(product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if(!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        product.setImageUrl(imageUrl);
    }

    @Override
    public void markAsSold(UUID productId) {
        Product product = getProductById(productId);
        if(product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if(!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        if(product.isSold()) {
            throw new IllegalStateException("Product is already sold");
        }
        product.setSold(true);
    }

     // ---------------- Delete ----------------

    @Override
    public void removeProduct(UUID productId) {
        if(productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product product = getProductById(productId);
        if(product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if(!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        products.removeIf(p -> p.getId().equals(productId));
    }

     // ---------------- Purchase Logic ----------------

    @Override
    public synchronized String purchaseProductByCustomer(UUID productId) {
        if(productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        if (product.isSold()) {
            throw new IllegalStateException("Product is already sold");
        }
        product.setSold(true);
        return product.getValue();
    }

    @Override
    public synchronized String purchaseProductByReseller(UUID productId, double resellerPrice) {
        if(productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (!products.contains(product)) {
            throw new IllegalArgumentException("Product not found in repository");
        }
        if (product.isSold()) {
            throw new IllegalStateException("Product is already sold");
        }
        if (resellerPrice < product.getMinimumSellPrice()) {
            throw new IllegalArgumentException("Reseller price must be at least the minimum sell price");
        }
        product.setSold(true);
        return product.getValue();
    }

}
