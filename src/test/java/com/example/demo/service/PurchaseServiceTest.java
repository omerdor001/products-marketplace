package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.facades.ProductFacade;
import com.example.demo.security.JWTTokenValidator;

public class PurchaseServiceTest {
    private PurchaseService purchaseService;

    @Mock
    private ProductFacade productFacade;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        purchaseService = PurchaseService.getInstance(productFacade);
    }

    @AfterEach
    void tearDown() throws Exception {
        PurchaseService.resetInstance();
        mocks.close();
    }

    // ---------- Purchase Product By Customer ----------

    @Test
    void testPurchaseProductByCustomer() {
        UUID productId = UUID.randomUUID();
        String value = "Value1";
        when(productFacade.purchaseProductByCustomer(productId)).thenReturn(value);
        String result = purchaseService.purchaseProductByCustomer(productId);
        assertEquals("Value1", result);
        verify(productFacade).purchaseProductByCustomer(productId);
    }

    @Test
    void testPurchaseProductByCustomer_ProductExistsAndNotSold() {
        UUID productId = UUID.randomUUID();
        String value = "Value1";
        when(productFacade.purchaseProductByCustomer(productId)).thenReturn(value);
        String result = purchaseService.purchaseProductByCustomer(productId);
        assertNotNull(result);
        verify(productFacade).purchaseProductByCustomer(productId);
    }

    @Test
    void testPurchaseProductByCustomer_concurrentAccess() throws InterruptedException, ExecutionException {
        UUID productId = UUID.randomUUID();
        String value = "Value1";
        when(productFacade.purchaseProductByCustomer(productId))
                .thenReturn(value)
                .thenThrow(new IllegalStateException("Product is already sold"),
                        new IllegalStateException("Product is already sold"),
                        new IllegalStateException("Product is already sold"),
                        new IllegalStateException("Product is already sold"));
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(executor.submit(() -> {
                try {
                    return purchaseService.purchaseProductByCustomer(productId);
                } catch (IllegalStateException | IllegalArgumentException e) {
                    return e.getMessage();
                }
            }));
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        int successCount = 0;
        int soldExceptionCount = 0;
        for (Future<String> f : futures) {
            String resultThread = f.get();
            if ("Value1".equals(resultThread))
                successCount++;
            else if ("Product is already sold".equals(resultThread))
                soldExceptionCount++;
        }
        assertEquals(1, successCount, "Only one thread should succeed in purchasing");
        assertEquals(4, soldExceptionCount, "All other threads should fail because product is sold");
        verify(productFacade, times(5)).purchaseProductByCustomer(productId);
    }

    @Test
    void testPurchaseProductByCustomer_ProductExistsAndSold() {
        UUID productId = UUID.randomUUID();
        doThrow(new IllegalStateException("Product is already sold"))
                .when(productFacade).purchaseProductByCustomer(productId);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> purchaseService.purchaseProductByCustomer(productId));
        assertEquals("Product is already sold", ex.getMessage());
        verify(productFacade).purchaseProductByCustomer(productId);
    }

    @Test
    void testPurchaseProductByCustomer_ProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productFacade).purchaseProductByCustomer(productId);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseProductByCustomer(productId));
        assertEquals("Product not found", ex.getMessage());
        verify(productFacade).purchaseProductByCustomer(productId);
    }

    // ---------- Purchase Product By Reseller ----------
    @Test
    void testPurchaseProductByReseller() {
        UUID productId = UUID.randomUUID();
        String value = "Value1";
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        when(productFacade.purchaseProductByReseller(productId, 100.0))
                .thenReturn(value);
        String result = purchaseService.purchaseProductByReseller(productId, 100.0, validJwtToken);
        assertEquals(value, result);
        verify(productFacade).purchaseProductByReseller(productId, 100.0);
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldAndPriceValid() {
        UUID productId = UUID.randomUUID();
        String value = "Value1";
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        when(productFacade.purchaseProductByReseller(productId, 100.0))
                .thenReturn(value);
        String result = purchaseService.purchaseProductByReseller(productId, 100.0, validJwtToken);
        assertNotNull(result);
        verify(productFacade).purchaseProductByReseller(productId, 100.0);
    }

    @Test
    void testPurchaseProductByReseller_concurrentAccess() throws InterruptedException, ExecutionException {
        UUID productId = UUID.randomUUID();
        String value = "Value1";
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        when(productFacade.purchaseProductByReseller(productId, 15.0))
                .thenReturn(value)
                .thenThrow(new IllegalStateException("Product is already sold"),
                        new IllegalStateException("Product is already sold"),
                        new IllegalStateException("Product is already sold"),
                        new IllegalStateException("Product is already sold"));
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(executor.submit(() -> {
                try {
                    return purchaseService.purchaseProductByReseller(productId, 15.0, validJwtToken);
                } catch (IllegalStateException | IllegalArgumentException e) {
                    return e.getMessage();
                }
            }));
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        int successCount = 0;
        int soldExceptionCount = 0;
        for (Future<String> f : futures) {
            String resultThread = f.get();
            if ("Value1".equals(resultThread))
                successCount++;
            else if ("Product is already sold".equals(resultThread))
                soldExceptionCount++;
        }
        assertEquals(1, successCount);
        assertEquals(4, soldExceptionCount);
        verify(productFacade, times(5)).purchaseProductByReseller(productId, 15.0);
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldButPriceInvalid() {
        UUID productId = UUID.randomUUID();
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        doThrow(new IllegalArgumentException("Price cannot be negative"))
                .when(productFacade).purchaseProductByReseller(productId, -5.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseProductByReseller(productId, -5.0, validJwtToken));
        assertEquals("Price cannot be negative", ex.getMessage());
        verify(productFacade).purchaseProductByReseller(productId, -5.0);
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndSold() {
        UUID productId = UUID.randomUUID();
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        doThrow(new IllegalStateException("Product is already sold"))
                .when(productFacade).purchaseProductByReseller(productId, 100.0);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> purchaseService.purchaseProductByReseller(productId, 100.0, validJwtToken));
        assertEquals("Product is already sold", ex.getMessage());
        verify(productFacade).purchaseProductByReseller(productId, 100.0);
    }

    @Test
    void testPurchaseProductByReseller_ProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        doThrow(new IllegalArgumentException("Product not found"))
                .when(productFacade).purchaseProductByReseller(productId, 100.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseProductByReseller(productId, 100.0, validJwtToken));
        assertEquals("Product not found", ex.getMessage());
        verify(productFacade).purchaseProductByReseller(productId, 100.0);
    }

    @Test
    void testPurchaseProductByReseller_InvalidToken() {
        UUID productId = UUID.randomUUID();
        String invalidJwtToken = JWTTokenValidator.getInstance("this.is.an.invalid.token", 3600000).generateTokenForTesting("reseller2");
        doThrow(new SecurityException("Invalid token"))
                .when(productFacade).purchaseProductByReseller(productId, 100.0);
        SecurityException ex = assertThrows(SecurityException.class,
                () -> purchaseService.purchaseProductByReseller(productId, 100.0, invalidJwtToken));
        assertEquals("Invalid token", ex.getMessage());
        verify(productFacade).purchaseProductByReseller(productId, 100.0);
    }
}
