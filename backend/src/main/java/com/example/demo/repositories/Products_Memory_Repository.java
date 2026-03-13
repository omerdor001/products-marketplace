package com.example.demo.repositories;

import org.springframework.stereotype.Component;
import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Products_Memory_Repository implements ProductRepository {

    private final Map<UUID, Product> products;  
    private final AdminRepository adminRepository;

    private static Products_Memory_Repository instance;

    private Products_Memory_Repository() {
        this.products = new ConcurrentHashMap<>();
        this.adminRepository = Admins_Memory_Repository.getInstance();
    }

    public static synchronized Products_Memory_Repository getInstance() {
        if (instance == null) {
            instance = new Products_Memory_Repository();
        }
        return instance;
    }

    // For testing purposes only
    public static void resetInstance() {
        instance = null;
    }

    // ---------------- Add ----------------

    @Override
    public void addCoupon(UUID id,String username, String name, String description, String imageUrl,
                          double costPrice, double marginPercentage, Coupon.ValueType valueType, String value) {

        if (!adminRepository.isAdminLoggedIn(username)) {
            throw new IllegalArgumentException("Admin not logged in");
        }
        Coupon coupon = new Coupon(id,name, description, imageUrl, costPrice, marginPercentage, valueType, value);
        products.put(id, coupon);
    }

    @Override
    public Product saveProduct(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public UUID addCoupon(String username, String name, String description, String imageUrl,
                          double costPrice, double marginPercentage, Coupon.ValueType valueType, String value) {

        return null;
    }

    // ---------------- Retrieve ----------------

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Product getProductById(UUID productId) {
        if (productId == null) return null;
        return products.get(productId);
    }

    @Override
    public List<Product> getAvailableProducts() {
        List<Product> available = new ArrayList<>();
        for (Product product : products.values()) {
            if (!product.isSold()) {
                available.add(product);
            }
        }
        return available;
    }

    // ---------------- Update ----------------

    @Override
    public void updateCouponCostPrice(String username, UUID productId, double costPrice) {
        validateAdmin(username);
        Product product = getExistingProduct(productId);
        if (costPrice < 0) throw new IllegalArgumentException("Cost price cannot be negative");
        product.setCostPrice(costPrice);
    }

    @Override
    public void updateCouponMarginPercentage(String username, UUID productId, double marginPercentage) {
        validateAdmin(username);
        Product product = getExistingProduct(productId);
        if (marginPercentage < 0 || marginPercentage > 100)
            throw new IllegalArgumentException("Margin percentage must be between 0 and 100");
        product.setMarginPercentage(marginPercentage);
    }

    @Override
    public void updateCouponValue(String username, UUID productId, Coupon.ValueType valueType, String value) {
        validateAdmin(username);
        Product product = getExistingProduct(productId);
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Value cannot be null or empty");
        product.setValueType(valueType);
        product.setValue(value);
    }

    @Override
    public void updateImageURL(String username, UUID productId, String imageUrl) {
        validateAdmin(username);
        Product product = getExistingProduct(productId);
        if (imageUrl == null || imageUrl.trim().isEmpty()) throw new IllegalArgumentException("Image URL cannot be null or empty");
        product.setImageUrl(imageUrl);
    }

    @Override
    public void markAsSold(UUID productId) {
        Product product = getExistingProduct(productId);
        if (product.isSold()) throw new IllegalStateException("Product is already sold");
        product.setSold(true);
    }

    // ---------------- Delete ----------------

    @Override
    public void removeProduct(String username, UUID productId) {
        validateAdmin(username);
        Product product = getExistingProduct(productId);
        products.remove(productId);
    }

    // ---------------- Purchase Logic ----------------

    @Override
    public synchronized String purchaseProductByCustomer(UUID productId) {
        Product product = getExistingProduct(productId);
        if (product.isSold()) throw new IllegalStateException("Product is already sold");
        product.setSold(true);
        return product.getValue();
    }

    @Override
    public synchronized double purchaseProductByReseller(UUID productId, double resellerPrice) {
        Product product = getExistingProduct(productId);
        if (product.isSold()) throw new IllegalStateException("Product is already sold");
        if (resellerPrice < product.getMinimumSellPrice())
            throw new IllegalArgumentException("Reseller price must be at least the minimum sell price");
        product.setSold(true);
        return resellerPrice;
    }

    // ---------------- Helper Methods ----------------

    public String getValueType(UUID productId){
        Product product = getExistingProduct(productId);
        return product.getValueType().name();
    }

    private void validateAdmin(String username) {
        if (!adminRepository.isAdminLoggedIn(username))
            throw new IllegalArgumentException("Admin not logged in");
    }

    private Product getExistingProduct(UUID productId) {
        Product product = products.get(productId);
        if (product == null) throw new IllegalArgumentException("Product not found");
        return product;
    }
}