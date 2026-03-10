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
import static org.mockito.Mockito.never;
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
import com.example.demo.domain.Coupon.ValueType;
import com.example.demo.domain.Product;
import com.example.demo.facades.ProductFacade;
import com.example.demo.security.JWTTokenValidator;

public class ProductServiceTest {

        private ProductService productService;
        @Mock
        private ProductFacade productFacade;

        private AutoCloseable mocks;

        private final String admin = "admin1";

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
                doNothing().when(productFacade).addCoupon(anyString(), anyString(), anyString(), anyString(),
                                anyDouble(), anyDouble(),
                                any(Coupon.ValueType.class),
                                anyString());
                productService.addCoupon("admin1", "Product1", "Description1", "http://image.url/1",
                                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
                verify(productFacade).addCoupon(
                                eq("admin1"),
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
                                .when(productFacade).addCoupon(anyString(),
                                                eq(null), anyString(), anyString(), anyDouble(), anyDouble(),
                                                any(Coupon.ValueType.class), anyString());

                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.addCoupon("admin1", null, "Description1", "http://image.url/1",
                                                10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
                assertEquals("Coupon name cannot be null or empty", ex.getMessage());
                verify(productFacade).addCoupon(
                                eq("admin1"), eq(null), eq("Description1"), eq("http://image.url/1"),
                                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
        }

        @Test
        void testAddCoupon_EmptyName_ShouldThrow() {
                doThrow(new IllegalArgumentException("Coupon name cannot be null or empty"))
                                .when(productFacade).addCoupon(
                                                eq("admin1"), eq("  "), anyString(), anyString(), anyDouble(),
                                                anyDouble(),
                                                any(Coupon.ValueType.class), anyString());

                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.addCoupon("admin1", "  ", "Description1", "http://image.url/1",
                                                10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
                assertEquals("Coupon name cannot be null or empty", ex.getMessage());
                verify(productFacade).addCoupon(
                                eq("admin1"), eq("  "), eq("Description1"), eq("http://image.url/1"),
                                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
        }

        @Test
        void testAddCoupon_NullDescription_ShouldThrow() {
                doThrow(new IllegalArgumentException("Coupon description cannot be null or empty"))
                                .when(productFacade).addCoupon(anyString(),
                                                anyString(), eq(null), anyString(), anyDouble(), anyDouble(),
                                                any(Coupon.ValueType.class), anyString());

                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.addCoupon("admin1", "Product", null, "http://image.url/1",
                                                10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
                assertEquals("Coupon description cannot be null or empty", ex.getMessage());
                verify(productFacade).addCoupon(
                                eq("admin1"), eq("Product"), eq(null), eq("http://image.url/1"),
                                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
        }

        @Test
        void testAddCoupon_EmptyDescription_ShouldThrow() {
                doThrow(new IllegalArgumentException("Coupon description cannot be null or empty"))
                                .when(productFacade).addCoupon(anyString(),
                                                anyString(), eq(" "), anyString(), anyDouble(), anyDouble(),
                                                any(Coupon.ValueType.class), anyString());

                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.addCoupon("admin1", "Product", " ", "http://image.url/1",
                                                10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
                assertEquals("Coupon description cannot be null or empty", ex.getMessage());
                verify(productFacade).addCoupon(
                                eq("admin1"), eq("Product"), eq(" "), eq("http://image.url/1"),
                                eq(10.0), eq(20.0), eq(Coupon.ValueType.STRING), eq("Value1"));
        }

        @Test
        void testAddCoupon_NullImageUrl_ShouldThrow() {
                doThrow(new IllegalArgumentException("Image URL cannot be null or empty"))
                                .when(productFacade).addCoupon(anyString(),
                                                anyString(), anyString(), eq(null), anyDouble(), anyDouble(),
                                                any(Coupon.ValueType.class), anyString());
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.addCoupon("admin1", "Product", "Description", null, 10.0, 20.0,
                                                Coupon.ValueType.STRING, "Value1"));
                assertEquals("Image URL cannot be null or empty", ex.getMessage());
                verify(productFacade).addCoupon(
                                eq("admin1"), eq("Product"), eq("Description"), eq(null),
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
        void testGetAvailableProducts_Authorized() {
                String validJwtToken = JWTTokenValidator
                                .getInstance("testsecretkeytestsecretkey", 3600000)
                                .generateTokenForTesting("reseller1");
                List<Product> mockProducts = Arrays.asList(
                                new Coupon("Name", "Desc", "http://img.com", 10.0, 20.0, ValueType.STRING, "100"),
                                new Coupon("Name 2", "Desc 2", "http://img2.com", 15.0, 25.0, ValueType.STRING, "101"));
                when(productFacade.getAvailableProducts()).thenReturn(mockProducts);
                List<Product> result = productService.getAvailableProducts(validJwtToken);
                assertEquals(2, result.size());
                assertEquals("Name", result.get(0).getName());
                assertEquals(15.0, result.get(1).getCostPrice());
                verify(productFacade).getAvailableProducts();
        }

        @Test
        void testGetAvailableProducts_Unauthorized() {
                String invalidToken = "invalid-token";
                assertThrows(SecurityException.class, () -> {
                        productService.getAvailableProducts(invalidToken);
                });
                verify(productFacade, never()).getAvailableProducts();
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
        void testGetProductById_Authorized() {
                UUID productId = UUID.randomUUID();
                String validJwtToken = JWTTokenValidator
                                .getInstance("testsecretkeytestsecretkey", 3600000)
                                .generateTokenForTesting("reseller1");
                Product product = new Coupon(
                                "Name", "Desc", "http://img.com",
                                10.0, 20.0, ValueType.STRING, "100");
                when(productFacade.getProductById(productId)).thenReturn(product);
                Product result = productService.getProductById(productId, validJwtToken);
                assertEquals(product, result);
                verify(productFacade).getProductById(productId);
        }

        @Test
        void getProductById_ShouldThrowUnauthorized_WhenTokenInvalid() {
                UUID productId = UUID.randomUUID();
                String invalidToken = "invalid-token";
                SecurityException exception = assertThrows(SecurityException.class, () -> {
                        productService.getProductById(productId, invalidToken);
                });
                assertEquals("Invalid or expired token", exception.getMessage());
                verify(productFacade, never()).getProductById(any());
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
                doNothing().when(productFacade).removeProduct(admin, productId);
                productService.removeProduct(admin, productId);
                verify(productFacade).removeProduct(admin, productId);
        }

        @Test
        void testRemoveProduct_ProductDoesNotExist() {
                UUID productId = UUID.randomUUID();
                doThrow(new IllegalArgumentException("Product not found"))
                                .when(productFacade).removeProduct(admin, productId);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.removeProduct(admin, productId));
                assertEquals("Product not found", ex.getMessage());
                verify(productFacade).removeProduct(admin, productId);
        }

        // ---------- Update Coupon Cost Price ----------
        @Test
        void testUpdateCouponCostPrice_ProductExistsAndPriceValid() {
                UUID productId = UUID.randomUUID();
                double newPrice = 50.0;
                doNothing().when(productFacade).updateCouponCostPrice(admin, productId, newPrice);
                productService.updateCouponCostPrice(admin, productId, newPrice);
                verify(productFacade).updateCouponCostPrice(admin, productId, newPrice);
        }

        @Test
        void testUpdateCouponCostPrice_ProductExistsButPriceInvalid() {
                UUID productId = UUID.randomUUID();
                double invalidPrice = -5.0;
                doThrow(new IllegalArgumentException("Cost price cannot be negative"))
                                .when(productFacade).updateCouponCostPrice(admin, productId, invalidPrice);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateCouponCostPrice(admin, productId, invalidPrice));
                assertEquals("Cost price cannot be negative", ex.getMessage());
                verify(productFacade).updateCouponCostPrice(admin, productId, invalidPrice);
        }

        @Test
        void testUpdateCouponCostPrice_ProductDoesNotExist() {
                UUID productId = UUID.randomUUID();
                double newPrice = 50.0;
                doThrow(new IllegalArgumentException("Product not found"))
                                .when(productFacade).updateCouponCostPrice(admin, productId, newPrice);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateCouponCostPrice(admin, productId, newPrice));
                assertEquals("Product not found", ex.getMessage());
                verify(productFacade).updateCouponCostPrice(admin, productId, newPrice);
        }

        // ---------- Update Coupon Margin Percentage ----------
        @Test
        void testUpdateCouponMarginPercentage_ProductExistsAndPercentageValid() {
                UUID productId = UUID.randomUUID();
                double newMargin = 30.0;
                doNothing().when(productFacade).updateCouponMarginPercentage(admin, productId, newMargin);
                productService.updateCouponMarginPercentage(admin, productId, newMargin);
                verify(productFacade).updateCouponMarginPercentage(admin, productId, newMargin);
        }

        @Test
        void testUpdateCouponMarginPercentage_ProductExistsButPercentageInvalid() {
                UUID productId = UUID.randomUUID();
                double invalidMargin = -5.0;
                doThrow(new IllegalArgumentException("Margin percentage must be between 0 and 100"))
                                .when(productFacade).updateCouponMarginPercentage(admin, productId, invalidMargin);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateCouponMarginPercentage(admin, productId, invalidMargin));
                assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
                verify(productFacade).updateCouponMarginPercentage(admin, productId, invalidMargin);
        }

        @Test
        void testUpdateCouponMarginPercentage_ProductDoesNotExist() {
                UUID nonExistentId = UUID.randomUUID();
                double margin = 20.0;
                doThrow(new IllegalArgumentException("Product not found"))
                                .when(productFacade).updateCouponMarginPercentage(admin, nonExistentId, margin);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateCouponMarginPercentage(admin, nonExistentId, margin));
                assertEquals("Product not found", ex.getMessage());
                verify(productFacade).updateCouponMarginPercentage(admin, nonExistentId, margin);
        }

        // ---------- Update Coupon Value ----------
        @Test
        void testUpdateCouponValue_ProductExistsAndValueValid() {
                UUID productId = UUID.randomUUID();
                Coupon.ValueType valueType = Coupon.ValueType.STRING;
                String newValue = "Updated Value";
                doNothing().when(productFacade).updateCouponValue(admin, productId, valueType, newValue);
                productService.updateCouponValue(admin, productId, valueType, newValue);
                verify(productFacade).updateCouponValue(admin, productId, valueType, newValue);
        }

        @Test
        void testUpdateCouponValue_ProductExistsButValueInvalid() {
                UUID productId = UUID.randomUUID();
                doThrow(new IllegalArgumentException("Coupon value cannot be null or empty"))
                                .when(productFacade).updateCouponValue(admin, productId, null, null);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateCouponValue(admin, productId, null, null));
                assertEquals("Coupon value cannot be null or empty", ex.getMessage());
                verify(productFacade).updateCouponValue(admin, productId, null, null);
        }

        @Test
        void testUpdateCouponValue_ProductDoesNotExist() {
                UUID nonExistentId = UUID.randomUUID();
                String newValue = "Updated Value";
                doThrow(new IllegalArgumentException("Product not found"))
                                .when(productFacade)
                                .updateCouponValue(admin, nonExistentId, Coupon.ValueType.STRING, newValue);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateCouponValue(admin, nonExistentId, Coupon.ValueType.STRING,
                                                newValue));
                assertEquals("Product not found", ex.getMessage());
                verify(productFacade).updateCouponValue(admin, nonExistentId, Coupon.ValueType.STRING, newValue);
        }

        // ---------- Update Image URL ----------
        @Test
        void testUpdateImageURL_ProductExistsAndURLValid() {
                UUID productId = UUID.randomUUID();
                String newUrl = "http://new.image.url/1";
                doNothing().when(productFacade).updateImageURL(admin, productId, newUrl);
                productService.updateImageURL(admin, productId, newUrl);
                verify(productFacade).updateImageURL(admin, productId, newUrl);
        }

        @Test
        void testUpdateImageURL_ProductExistsButURLInvalid() {
                UUID productId = UUID.randomUUID();
                doThrow(new IllegalArgumentException("Image URL cannot be null or empty"))
                                .when(productFacade).updateImageURL(admin, productId, null);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateImageURL(admin, productId, null));
                assertEquals("Image URL cannot be null or empty", ex.getMessage());
                verify(productFacade).updateImageURL(admin, productId, null);
        }

        @Test
        void testUpdateImageURL_ProductDoesNotExist() {
                UUID nonExistentId = UUID.randomUUID();
                String newUrl = "http://new.image.url/1";
                doThrow(new IllegalArgumentException("Product not found"))
                                .when(productFacade).updateImageURL(admin, nonExistentId, newUrl);
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                                () -> productService.updateImageURL(admin, nonExistentId, newUrl));
                assertEquals("Product not found", ex.getMessage());
                verify(productFacade).updateImageURL(admin, nonExistentId, newUrl);
        }
}
