package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.facades.ProductFacade;
import com.example.demo.security.JWTTokenValidator;

@Service
public class ProductService {

    private static ProductService instance;
    private ProductFacade productFacade;
    private JWTTokenValidator tokenValidator;

    private ProductService(ProductFacade productsFacade) {
        this.productFacade = productsFacade;
        String secret = "mySuperSecureSecretKeyThatIsAtLeast32Bytes!";
        tokenValidator = JWTTokenValidator.getInstance(secret, 3600000);
    }

    public static ProductService getInstance(ProductFacade facade) {
        if (instance == null) {
            instance = new ProductService(facade);
        }
        return instance;
    }

    // For testing purposes only
    public static void resetInstance() {
        instance = null;
    }

    public void addCoupon(String username, String name, String description, String imageUrl,
                          double costPrice, double marginPercentage,
                          Coupon.ValueType valueType, String value) {

        productFacade.addCoupon(username,name, description, imageUrl,
                costPrice, marginPercentage, valueType, value);
    }

    public void removeProduct(String username, UUID productId) {
        productFacade.removeProduct(username, productId);
    }

    public List<Product> getAllProducts() {
        return productFacade.getAllProducts();
    }

    public Product getProductById(UUID productId) {
        return productFacade.getProductById(productId);
    }

    public List<Product> getAvailableProducts() {
        return productFacade.getAvailableProducts();
    }

    public Product getProductById(UUID productId,String token) { 
        if (!tokenValidator.isValid(token)) {
            throw new SecurityException("Invalid or expired token");
        }
        return productFacade.getProductById(productId);
    }

    public List<Product> getAvailableProducts(String token) { 
        if (!tokenValidator.isValid(token)) {
            throw new SecurityException("Invalid or expired token");
        }
        return productFacade.getAvailableProducts();
    }

    public void updateCouponCostPrice(String username, UUID productId, double costPrice) {
        productFacade.updateCouponCostPrice(username, productId, costPrice);
    }

    public void updateCouponMarginPercentage(String username, UUID productId, double marginPercentage) {
        productFacade.updateCouponMarginPercentage(username, productId, marginPercentage);
    }

    public void updateCouponValue(String username, UUID productId, Coupon.ValueType valueType, String value) {
        productFacade.updateCouponValue(username, productId, valueType, value);
    }

    public void updateImageURL(String username, UUID productId, String imageUrl) {
        productFacade.updateImageURL(username, productId, imageUrl);
    }

    public void markAsSold(UUID productId) {
        productFacade.markAsSold(productId);
    }
}