package com.example.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.facades.ProductFacade;
import com.example.demo.security.JWTTokenValidator;

@Service
public class PurchaseService {

    private static PurchaseService instance;
    private ProductFacade productFacade;
    private JWTTokenValidator tokenValidator;

    private PurchaseService(ProductFacade productFacade) {
        this.productFacade = productFacade;
        String secret = "mySuperSecureSecretKeyThatIsAtLeast32Bytes!";
        tokenValidator = JWTTokenValidator.getInstance(secret, 3600000);
    }

    public static PurchaseService getInstance(ProductFacade facade) {
        if (instance == null) {
            instance = new PurchaseService(facade);
        }
        return instance;
    }

    // For testing purposes only
    public static void resetInstance() {
        instance = null;
    }

    public String purchaseProductByCustomer(UUID productId) {
        return productFacade.purchaseProductByCustomer(productId);
    }

    public double purchaseProductByReseller(UUID productId, double resellerPrice, String token) {
        return productFacade.purchaseProductByReseller(productId, resellerPrice);    //change to value
    }
}