package com.example.demo.repositories;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.data_access.JpaCouponRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class Products_DB_Repository implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JpaCouponRepository dbRepository;

    public Products_DB_Repository(JpaCouponRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    // ---------------- Add ----------------

    @Override
    public void addCoupon(String name, String description, String imageUrl, double costPrice,
            double marginPercentage, Coupon.ValueType valueType, String value) {
        Coupon coupon = new Coupon(name, description, imageUrl, costPrice, marginPercentage, valueType, value);
        entityManager.persist(coupon);
    }

    @Override
    public Product saveProduct(Product product) {
        return entityManager.merge(product);
    }

    // ---------------- Retrieve ----------------

    @Override
    public List<Product> getAllProducts() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    @Override
    public Product getProductById(UUID productId) {
        Product product = entityManager.find(Product.class, productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        return product;
    }

    @Override
    public List<Product> getAvailableProducts() {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.sold = false", Product.class)
                .getResultList();
    }

    // ---------------- Update ----------------

    @Override
    public void updateCouponCostPrice(UUID productId, double costPrice) {
        Product p = entityManager.find(Product.class, productId);
        if (p != null) {
            p.setCostPrice(costPrice);
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }

    @Override
    public void updateCouponMarginPercentage(UUID productId, double marginPercentage) {
        Product p = getProductById(productId);
        if (p != null) {
            p.setMarginPercentage(marginPercentage);
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }

    @Override
    public void updateCouponValue(UUID productId, Coupon.ValueType valueType, String value) {
        Product p = getProductById(productId);
        if (p != null) {
            p.setValueType(valueType);
            p.setValue(value);
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }

    @Override
    public void updateImageURL(UUID productId, String imageUrl) {
        Product p = getProductById(productId);
        if (p != null) {
            p.setImageUrl(imageUrl);
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }

    @Override
    public void markAsSold(UUID productId) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        product.setSold(true);
    }

    // ---------------- Delete ----------------

    @Override
    public void removeProduct(UUID productId) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        entityManager.remove(product);
    }

    // ---------------- Purchase Logic ----------------

    @Override
    public synchronized String purchaseProductByCustomer(UUID productId) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (product.isSold()) {
            throw new IllegalStateException("Product is already sold");
        }
        product.setSold(true);
        return product.getValue();
    }

    @Override
    public synchronized String purchaseProductByReseller(UUID productId, double resellerPrice) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (resellerPrice < product.getCostPrice()) {
            throw new IllegalArgumentException("Reseller price cannot be less than cost price");
        }
        product.setSold(true);
        return product.getValue();
    }

    // ---------------- Additional helper ----------------

    public boolean exists(UUID productId) {
        return dbRepository.existsById(productId);
    }
}