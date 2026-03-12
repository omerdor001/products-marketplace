
package com.example.demo.UT.facades;

import com.example.demo.domain.Coupon;
import com.example.demo.facades.ProductFacade;
import com.example.demo.repositories.Products_DB_Repository;
import com.example.demo.repositories.Products_Memory_Repository;
import com.example.demo.repositories.AdminRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductFacadeTest {

    private ProductFacade facade;
    private Products_DB_Repository dbRepository;
    private Products_Memory_Repository memoryRepository;
    private AdminRepository adminRepository;

    @BeforeEach
    void setup() {
        dbRepository = mock(Products_DB_Repository.class);
        memoryRepository = mock(Products_Memory_Repository.class);
        adminRepository = mock(AdminRepository.class);
        facade = new ProductFacade(dbRepository, memoryRepository);
        when(adminRepository.isAdminLoggedIn("admin1")).thenReturn(true);
    }

    @Test
    void testAddCoupon_emptyName_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "  ",
                        "Valid description", "http://image.jpg",
                        10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));

        assertEquals("Coupon name cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_nullDescription_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        null, "http://image.jpg",
                        10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));

        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_emptyDescription_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        " ", "http://image.jpg",
                        10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));

        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_nullImageUrl_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        "Description", null,
                        10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));

        assertEquals("Image URL cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_negativeCostPrice_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        "Description", "http://image.jpg",
                        -5.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));

        assertEquals("Cost price cannot be negative", ex.getMessage());
    }

    @Test
    void testAddCoupon_invalidMarginPercentageBelowZero_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        "Description", "http://image.jpg",
                        10.0, -5.0, Coupon.ValueType.STRING, "10$ OFF"));

        assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
    }

    @Test
    void testAddCoupon_invalidMarginPercentageAboveHundred_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        "Description", "http://image.jpg",
                        10.0, 120.0, Coupon.ValueType.STRING, "10$ OFF"));

        assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
    }

    @Test
    void testAddCoupon_nullValueType_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        "Description", "http://image.jpg",
                        10.0, 20.0, null, "10$ OFF"));

        assertEquals("Value type cannot be null", ex.getMessage());
    }

    @Test
    void testAddCoupon_nullValue_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        "Description", "http://image.jpg",
                        10.0, 20.0, Coupon.ValueType.STRING, null));

        assertEquals("Coupon value cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_emptyValue_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> facade.addCoupon("admin1", "Coupon",
                        "Description", "http://image.jpg",
                        10.0, 20.0, Coupon.ValueType.STRING, "  "));

        assertEquals("Coupon value cannot be null or empty", ex.getMessage());
    }
}

