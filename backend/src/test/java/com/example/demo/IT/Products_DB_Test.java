package com.example.demo.IT;

import com.example.demo.data_access.JpaCouponRepository;
import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.repositories.Products_DB_Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class Products_DB_Test {

    @Autowired
    private JpaCouponRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    private Products_DB_Repository repository;

    private final String admin = "admin1";

    @BeforeEach
    void setup() {
        repository = new Products_DB_Repository(jpaRepository);
        repository.setEntityManager(entityManager); 
    }

    // ---------------- Add ----------------

    @Test
    void addCoupon_shouldPersistCoupon() {
        repository.addCoupon(admin,"TestCoupon", "desc", "img",
                10.0, 20.0, Coupon.ValueType.STRING, "VALUE");
        List<Product> products = repository.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("TestCoupon", products.get(0).getName());
    }

    // ---------------- Get All ----------------

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        repository.addCoupon(admin,"C1", "d", "img", 10.0, 10.0, Coupon.ValueType.STRING, "A");
        repository.addCoupon(admin,"C2", "d", "img", 20.0, 20.0, Coupon.ValueType.STRING, "B");
        List<Product> products = repository.getAllProducts();
        assertEquals(2, products.size());
    }

    // ---------------- Remove ----------------

    @Test
    void removeProduct_shouldDeleteProduct() {
        repository.addCoupon(admin,"C1", "d", "img", 10.0, 10.0, Coupon.ValueType.STRING, "A");
        Product product = repository.getAllProducts().get(0);
        UUID id = product.getId();
        repository.removeProduct(admin, id);
        assertTrue(repository.getAllProducts().isEmpty());
    }

    // ---------------- Updates ----------------

    @Test
    void updateCouponCostPrice_shouldUpdateCost() {
        repository.addCoupon(admin,"C1", "d", "img", 10.0, 10.0, Coupon.ValueType.STRING, "A");
        Product product = repository.getAllProducts().get(0);
        repository.updateCouponCostPrice(admin, product.getId(), 50);
        Product updated = repository.getProductById(product.getId());
        assertEquals(50, updated.getCostPrice());
    }

    @Test
    void updateCouponMarginPercentage_shouldUpdateMargin() {
        repository.addCoupon(admin,"C1", "d", "img", 10.0, 10.0, Coupon.ValueType.STRING, "A");
        Product product = repository.getAllProducts().get(0);
        repository.updateCouponMarginPercentage(admin, product.getId(), 35);
        Product updated = repository.getProductById(product.getId());
        assertEquals(35, updated.getMarginPercentage());
    }

    @Test
    void updateCouponValue_shouldUpdateValue() {
        repository.addCoupon(admin,"C1", "d", "img", 10.0, 10.0, Coupon.ValueType.IMAGE, "A");
        Product product = repository.getAllProducts().get(0);
        repository.updateCouponValue(admin, product.getId(),
                Coupon.ValueType.STRING, "30");
        Product updated = repository.getProductById(product.getId());
        assertEquals(Coupon.ValueType.STRING, updated.getValueType());
        assertEquals("30", updated.getValue());
    }

    @Test
    void updateImageURL_shouldUpdateImage() {
        repository.addCoupon(admin,"C1", "d", "img", 10.0, 10.0, Coupon.ValueType.STRING, "A");
        Product product = repository.getAllProducts().get(0);
        repository.updateImageURL(admin, product.getId(), "newImage");
        Product updated = repository.getProductById(product.getId());
        assertEquals("newImage", updated.getImageUrl());
    }

    @Test
    void markAsSold_shouldMarkProductSold() {
        repository.addCoupon(admin,"C1", "d", "img", 10.0, 10.0, Coupon.ValueType.STRING, "A");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        Product updated = repository.getProductById(product.getId());
        assertTrue(updated.isSold());
    }
}