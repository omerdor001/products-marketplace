package com.example.demo.repositories;

import java.util.List;
import java.util.UUID;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;

public interface ProductRepository {
    void addCoupon(String username ,String name, String description, String imageUrl,
                   double costPrice, double marginPercentage,
                   Coupon.ValueType valueType, String value);

    Product saveProduct(Product product); 
    List<Product> getAllProducts();
    Product getProductById(UUID productId);
    List<Product> getAvailableProducts();
    void updateCouponCostPrice(String username ,UUID productId, double costPrice);
    void updateCouponMarginPercentage(String username ,UUID productId, double marginPercentage);
    void updateCouponValue(String username ,UUID productId, Coupon.ValueType valueType, String value);
    void updateImageURL(String username,UUID productId, String imageUrl);
    void markAsSold(UUID productId);
    void removeProduct(String username,UUID productId);
    String purchaseProductByCustomer(UUID productId);
    double purchaseProductByReseller(UUID productId, double resellerPrice);

}
