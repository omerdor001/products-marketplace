package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.security.JWTTokenValidator;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;

public class PurchaseServiceTest {
    private PurchaseService purchaseService;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        purchaseService = PurchaseService.getInstance();
        productService = ProductService.getInstance();

    }

    @AfterEach
    void tearDown() {
       PurchaseService.resetInstance();
       ProductService.resetInstance();
    }
    
    // ---------- Purchase Product By Customer ----------
    @Test
    void testPurchaseProductByCustomer() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = productService.getAllProducts().get(0);
        String value = purchaseService.purchaseProductByCustomer(product.getId());
        assertEquals("Value1", value);
    }

    @Test
    void testPurchaseProductByCustomer_ProductExistsAndNotSold() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = productService.getAllProducts().get(0);
        String value = purchaseService.purchaseProductByCustomer(product.getId());
        assertNotNull(value);
    }

    @Test
    void testPurchaseProductByCustomer_concurrentAccess() throws InterruptedException, ExecutionException {
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        UUID productId = productService.getAllProducts().get(0).getId();
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
            String result = f.get();
            if ("Value1".equals(result)) successCount++;
            else if ("Product is already sold".equals(result)) soldExceptionCount++;
        }
        assertEquals(1, successCount, "Only one thread should succeed in purchasing");
        assertEquals(4, soldExceptionCount, "All other threads should fail because product is sold");
    }

    @Test
    void testPurchaseProductByCustomer_ProductExistsAndSold() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = productService.getAllProducts().get(0);
        productService.markAsSold(product.getId());
        assertThrows(IllegalStateException.class,
                () -> purchaseService.purchaseProductByCustomer(product.getId()));
    }

    @Test
    void testPurchaseProductByCustomer_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseProductByCustomer(UUID.randomUUID()));
    }

    // ---------- Purchase Product By Reseller ----------
    @Test
    void testPurchaseProductByReseller() {
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = productService.getAllProducts().get(0);
        String value = purchaseService.purchaseProductByReseller(product.getId(), 100.0, validJwtToken);
        assertEquals("Value1", value);
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldAndPriceValid() {
                String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = productService.getAllProducts().get(0);
        String value = purchaseService.purchaseProductByReseller(product.getId(), 100.0, validJwtToken);
        assertNotNull(value);
    }

    @Test
    void testPurchaseProductByReseller_concurrentAccess() throws InterruptedException, ExecutionException {
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        UUID productId = productService.getAllProducts().get(0).getId();
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
            String result = f.get();
            if ("Value1".equals(result)) successCount++;
            else if ("Product is already sold".equals(result)) soldExceptionCount++;
        }
        assertEquals(1, successCount, "Only one thread should succeed in purchasing");
        assertEquals(4, soldExceptionCount, "Other threads should fail because product is already sold");
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldButPriceInvalid() {
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = productService.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseProductByReseller(product.getId(), -5.0, validJwtToken));
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndSold() {
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = productService.getAllProducts().get(0);
        productService.markAsSold(product.getId());
        assertThrows(IllegalStateException.class,
                () -> purchaseService.purchaseProductByReseller(product.getId(), 100.0, validJwtToken));
    }

    @Test
    void testPurchaseProductByReseller_ProductDoesNotExist() {
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseProductByReseller(UUID.randomUUID(), 100.0, validJwtToken));
    }

    @Test
void testPurchaseProductByReseller_InvalidToken() {
    productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING, "Value1");
    Product product = productService.getAllProducts().get(0);
    String invalidToken = "this.is.an.invalid.token";
    assertThrows(SecurityException.class, () -> 
        purchaseService.purchaseProductByReseller(product.getId(), 100.0, invalidToken)
    );
}
}
