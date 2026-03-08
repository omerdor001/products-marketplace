package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.facades.ProductFacade;


public class ProductService {

    private static ProductService instance;
    private ProductFacade productFacade;

    private ProductService(ProductFacade productsFacade) {
        this.productFacade = productsFacade;
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

    public void addCoupon(String name, String description, String imageUrl,
                          double costPrice, double marginPercentage,
                          Coupon.ValueType valueType, String value) {

        productFacade.addCoupon(name, description, imageUrl,
                costPrice, marginPercentage, valueType, value);
    }

    public void removeProduct(UUID productId) {
        productFacade.removeProduct(productId);
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

    public void updateCouponCostPrice(UUID productId, double costPrice) {
        productFacade.updateCouponCostPrice(productId, costPrice);
    }

    public void updateCouponMarginPercentage(UUID productId, double marginPercentage) {
        productFacade.updateCouponMarginPercentage(productId, marginPercentage);
    }

    public void updateCouponValue(UUID productId, Coupon.ValueType valueType, String value) {
        productFacade.updateCouponValue(productId, valueType, value);
    }

    public void updateImageURL(UUID productId, String imageUrl) {
        productFacade.updateImageURL(productId, imageUrl);
    }

    public void markAsSold(UUID productId) {
        productFacade.markAsSold(productId);
    }
}