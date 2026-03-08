package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.facades.ProductFacade;

public class ProductServiceTest {

    private ProductService productService;
    @Mock
    private ProductFacade productFacade;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        productService = ProductService.getInstance(productFacade);
    }

    @AfterEach
    void tearDown() throws Exception {
        ProductService.resetInstance();
        mocks.close();
    }

    // ---------- Add Coupon ----------
    @Test
    void testAddCoupon_Success() {
        doNothing().when(productFacade).addCoupon(anyString(), anyString(), anyString(), anyDouble(), anyDouble(),
                any(Coupon.ValueType.class),
                anyString());
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        verify(productFacade).addCoupon(
                eq("Product1"),
                eq("Description1"),
                eq("http://image.url/1"),
                eq(10.0),
                eq(20.0),
                eq(Coupon.ValueType.STRING),
                eq("Value1"));
    }

    @Test
    void testAddCoupon_NullName_ShouldThrow() {
        doThrow(new IllegalArgumentException("Coupon name cannot be null or empty"))
                .when(productFacade).addCoupon(
                        eq(null), anyString(), anyString(), anyDouble(), anyDouble(),
                        any(Coupon.ValueType.class), anyString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon(null, "Description", "http://image.url/1",
                        10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon name cannot be null or empty", ex.getMessage());
        verify(productFacade).addCoupon(
                eq(null), eq("Description"), eq("http://image.url/1"),
                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
    }

    @Test
    void testAddCoupon_EmptyName_ShouldThrow() {
        doThrow(new IllegalArgumentException("Coupon name cannot be null or empty"))
                .when(productFacade).addCoupon(
                        eq("  "), anyString(), anyString(), anyDouble(), anyDouble(),
                        any(Coupon.ValueType.class), anyString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("  ", "Description", "http://image.url/1",
                        10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon name cannot be null or empty", ex.getMessage());
        verify(productFacade).addCoupon(
                eq("  "), eq("Description"), eq("http://image.url/1"),
                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
    }

    @Test
    void testAddCoupon_NullDescription_ShouldThrow() {
        doThrow(new IllegalArgumentException("Coupon description cannot be null or empty"))
                .when(productFacade).addCoupon(
                        anyString(), eq(null), anyString(), anyDouble(), anyDouble(),
                        any(Coupon.ValueType.class), anyString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", null, "http://image.url/1",
                        10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
        verify(productFacade).addCoupon(
                eq("Product"), eq(null), eq("http://image.url/1"),
                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
    }

    @Test
    void testAddCoupon_EmptyDescription_ShouldThrow() {
        doThrow(new IllegalArgumentException("Coupon description cannot be null or empty"))
                .when(productFacade).addCoupon(
                        anyString(), eq(" "), anyString(), anyDouble(), anyDouble(),
                        any(Coupon.ValueType.class), anyString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", " ", "http://image.url/1",
                        10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
        verify(productFacade).addCoupon(
                eq("Product"), eq(" "), eq("http://image.url/1"),
                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
    }

    @Test
    void testAddCoupon_NullImageUrl_ShouldThrow() {
        doThrow(new IllegalArgumentException("Image URL cannot be null or empty"))
                .when(productFacade).addCoupon(
                        anyString(), anyString(), eq(null), anyDouble(), anyDouble(),
                        any(Coupon.ValueType.class), anyString());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", null, 10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Image URL cannot be null or empty", ex.getMessage());
        verify(productFacade).addCoupon(
                eq("Product"), eq("Description"), eq(null),
                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
    }

    // ---------- Get Available Products ----------
    @Test
    void testGetAvailableProducts_AvailableExists() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Product product1 = mock(Product.class);
        when(product1.getId()).thenReturn(id1);
        when(product1.getName()).thenReturn("Product1");
        Product product2 = mock(Product.class);
        when(product2.getId()).thenReturn(id2);
        when(product2.getName()).thenReturn("Product2");
        when(productFacade.getAvailableProducts()).thenReturn(Arrays.asList(product2));
        List<Product> available = productService.getAvailableProducts();
        assertEquals(1, available.size());
        assertEquals("Product2", available.get(0).getName());
        verify(productFacade).getAvailableProducts();
    }

    @Test
    void testGetAvailableProducts_AllSold() {
        when(productFacade.getAvailableProducts()).thenReturn(List.of());
        List<Product> available = productService.getAvailableProducts();
        assertTrue(available.isEmpty(), "No products should be available when all are sold");
        verify(productFacade).getAvailableProducts();
    }

    // ---------- Get Product by ID ----------

    @Test
    void testGetProductById_ProductExists() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        when(product.getName()).thenReturn("Product1");
        when(productFacade.getProductById(productId)).thenReturn(product);
        Product fetched = productService.getProductById(productId);
        assertNotNull(fetched);
        assertEquals("Product1", fetched.getName());
        verify(productFacade).getProductById(productId);
    }

    @Test
    void testGetProductById_ProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        when(productFacade.getProductById(productId)).thenReturn(null);
        Product fetched = productService.getProductById(productId);
        assertNull(fetched);
        verify(productFacade).getProductById(productId);
    }

    // ---------- Remove Product ----------

    @Test
    void testRemoveProduct_ProductExists() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        doNothing().when(productFacade).removeProduct(productId);
        productService.removeProduct(productId);
        verify(productFacade).removeProduct(productId);
    }

    @Test
    void testRemoveProduct_ProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productFacade).removeProduct(productId);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.removeProduct(productId));
        assertEquals("Product not found", ex.getMessage());
        verify(productFacade).removeProduct(productId);
    }

    // ---------- Update Coupon Cost Price ----------
    @Test
    void testUpdateCouponCostPrice_ProductExistsAndPriceValid() {
        UUID productId = UUID.randomUUID();
        double newPrice = 50.0;
        doNothing().when(productFacade).updateCouponCostPrice(productId, newPrice);
        productService.updateCouponCostPrice(productId, newPrice);
        verify(productFacade).updateCouponCostPrice(productId, newPrice);
    }

    @Test
    void testUpdateCouponCostPrice_ProductExistsButPriceInvalid() {
        UUID productId = UUID.randomUUID();
        double invalidPrice = -5.0;
        doThrow(new IllegalArgumentException("Cost price cannot be negative"))
                .when(productFacade).updateCouponCostPrice(productId, invalidPrice);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponCostPrice(productId, invalidPrice));
        assertEquals("Cost price cannot be negative", ex.getMessage());
        verify(productFacade).updateCouponCostPrice(productId, invalidPrice);
    }

    @Test
    void testUpdateCouponCostPrice_ProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        double newPrice = 50.0;
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productFacade).updateCouponCostPrice(productId, newPrice);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponCostPrice(productId, newPrice));
        assertEquals("Product not found", ex.getMessage());
        verify(productFacade).updateCouponCostPrice(productId, newPrice);
    }

    // ---------- Update Coupon Margin Percentage ----------
    @Test
    void testUpdateCouponMarginPercentage_ProductExistsAndPercentageValid() {
        UUID productId = UUID.randomUUID();
        double newMargin = 30.0;
        doNothing().when(productFacade).updateCouponMarginPercentage(productId, newMargin);
        productService.updateCouponMarginPercentage(productId, newMargin);
        verify(productFacade).updateCouponMarginPercentage(productId, newMargin);
    }

    @Test
    void testUpdateCouponMarginPercentage_ProductExistsButPercentageInvalid() {
        UUID productId = UUID.randomUUID();
        double invalidMargin = -5.0;
        doThrow(new IllegalArgumentException("Margin percentage must be between 0 and 100"))
                .when(productFacade).updateCouponMarginPercentage(productId, invalidMargin);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponMarginPercentage(productId, invalidMargin));
        assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
        verify(productFacade).updateCouponMarginPercentage(productId, invalidMargin);
    }

    @Test
    void testUpdateCouponMarginPercentage_ProductDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        double margin = 20.0;
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productFacade).updateCouponMarginPercentage(nonExistentId, margin);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponMarginPercentage(nonExistentId, margin));
        assertEquals("Product not found", ex.getMessage());
        verify(productFacade).updateCouponMarginPercentage(nonExistentId, margin);
    }

    // ---------- Update Coupon Value ----------
    @Test
    void testUpdateCouponValue_ProductExistsAndValueValid() {
        UUID productId = UUID.randomUUID();
        Coupon.ValueType valueType = Coupon.ValueType.STRING;
        String newValue = "Updated Value";
        doNothing().when(productFacade).updateCouponValue(productId, valueType, newValue);
        productService.updateCouponValue(productId, valueType, newValue);
        verify(productFacade).updateCouponValue(productId, valueType, newValue);
    }

    @Test
    void testUpdateCouponValue_ProductExistsButValueInvalid() {
        UUID productId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Coupon value cannot be null or empty"))
                .when(productFacade).updateCouponValue(productId, null, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponValue(productId, null, null));
        assertEquals("Coupon value cannot be null or empty", ex.getMessage());
        verify(productFacade).updateCouponValue(productId, null, null);
    }

    @Test
    void testUpdateCouponValue_ProductDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        String newValue = "Updated Value";
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productFacade).updateCouponValue(nonExistentId, Coupon.ValueType.STRING, newValue);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponValue(nonExistentId, Coupon.ValueType.STRING, newValue));
        assertEquals("Product not found", ex.getMessage());
        verify(productFacade).updateCouponValue(nonExistentId, Coupon.ValueType.STRING, newValue);
    }

    // ---------- Update Image URL ----------
    @Test
    void testUpdateImageURL_ProductExistsAndURLValid() {
        UUID productId = UUID.randomUUID();
        String newUrl = "http://new.image.url/1";
        doNothing().when(productFacade).updateImageURL(productId, newUrl);
        productService.updateImageURL(productId, newUrl);
        verify(productFacade).updateImageURL(productId, newUrl);
    }

    @Test
    void testUpdateImageURL_ProductExistsButURLInvalid() {
        UUID productId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Image URL cannot be null or empty"))
                .when(productFacade).updateImageURL(productId, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateImageURL(productId, null));
        assertEquals("Image URL cannot be null or empty", ex.getMessage());
        verify(productFacade).updateImageURL(productId, null);
    }

    @Test
    void testUpdateImageURL_ProductDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        String newUrl = "http://new.image.url/1";
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productFacade).updateImageURL(nonExistentId, newUrl);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.updateImageURL(nonExistentId, newUrl));
        assertEquals("Product not found", ex.getMessage());
        verify(productFacade).updateImageURL(nonExistentId, newUrl);
    }
}
