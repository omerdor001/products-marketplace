package service;

import java.util.UUID;
import domain.Products_Memory_Repository;
import security.JWTTokenValidator;

public class PurchaseService {

    private static PurchaseService instance;
    private Products_Memory_Repository productsRepository;
    private JWTTokenValidator tokenValidator; 

    private PurchaseService() {
        productsRepository = new Products_Memory_Repository();
        tokenValidator = new JWTTokenValidator("supersecretkey12345678901234567890", 3600_000);
    }

    public static PurchaseService getInstance() {
        if (instance == null) {
            instance = new PurchaseService();
        }
        return instance;
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