package com.example.demo.service;

import java.util.UUID;

import com.example.demo.repositories.Products_Memory_Repository;
import com.example.demo.security.JWTTokenValidator;

public class PurchaseService {

    private static PurchaseService instance;
    private Products_Memory_Repository productsRepository;
    private JWTTokenValidator tokenValidator; 

    private PurchaseService() {
        productsRepository = Products_Memory_Repository.getInstance();
        String secret = "mySuperSecureSecretKeyThatIsAtLeast32Bytes!";
        tokenValidator = JWTTokenValidator.getInstance(secret, 3600000); 
    }

    public static PurchaseService getInstance() {
        if (instance == null) {
            instance = new PurchaseService();
        }
        return instance;
    }

    //For testing purposes only
    public static void resetInstance() {
        Products_Memory_Repository.resetInstance();
        instance = null;
    }

    public String purchaseProductByCustomer(UUID productId) {
        return productsRepository.purchaseProductByCustomer(productId);
    }

    public String purchaseProductByReseller(UUID productId, double resellerPrice, String token) {
        if (!tokenValidator.isValid(token)) {
            throw new SecurityException("Invalid or expired token");
        }
        return productsRepository.purchaseProductByReseller(productId, resellerPrice);
    }

}