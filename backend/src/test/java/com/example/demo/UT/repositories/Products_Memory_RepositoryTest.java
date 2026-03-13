package com.example.demo.UT.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.example.demo.domain.Coupon.ValueType;
import com.example.demo.repositories.Admins_Memory_Repository;
import com.example.demo.repositories.Products_Memory_Repository;

public class Products_Memory_RepositoryTest {
    private Products_Memory_Repository repository;

    @BeforeEach
    void setUp() {
        repository = Products_Memory_Repository.getInstance();
        Admins_Memory_Repository admin_repository = Admins_Memory_Repository.getInstance();
        admin_repository.addAdmin("admin1", "password123");
        admin_repository.addAdmin("admin2", "password456");
        admin_repository.login("admin1", "password123");
    }

    @AfterEach
    void tearDown() {
        Products_Memory_Repository.resetInstance();
        Admins_Memory_Repository.resetInstance();
    }

    // ---------- Add Coupon ----------
    @Test
    void testAddCoupon_Success() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        assert repository.getAllProducts().size() == 1;
    }

    @Test
    void testAddCoupon_EmptyAdmin() {
        UUID id = UUID.randomUUID();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repository.addCoupon(id," ", "Product1",
                        "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
        assertEquals("Username cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_NotLoggedAdmin() {
        UUID id = UUID.randomUUID();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repository.addCoupon(id,"admin2", "Product1",
                        "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING, "Value1"));
        assertEquals("Admin not logged in", ex.getMessage());
    }

    // ---------- Get Available Products ----------
    @Test
    void testGetAvailableProducts_AvailableExists() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        repository.addCoupon(id1,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        repository.addCoupon(id2,"admin1", "Product2", "Description2", "http://image.url/2", 15.0, 25.0,
                Coupon.ValueType.STRING,
                "Value2");
        repository.markAsSold(repository.getAllProducts().get(0).getId());
        assert repository.getAvailableProducts().size() == 1;
    }

    @Test
    void testGetAvailableProducts_AllSold() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        repository.addCoupon(id1,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        repository.addCoupon(id2,"admin1", "Product2", "Description2", "http://image.url/2", 15.0, 25.0,
                Coupon.ValueType.STRING,
                "Value2");
        repository.getAllProducts().forEach(p -> repository.markAsSold(p.getId()));
        assertTrue(repository.getAvailableProducts().isEmpty(), "No products should be available when all are sold");
    }

    // ---------- Get Product By Id ----------
    @Test
    void testGetProductById_ProductExists() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        Product fetched = repository.getProductById(product.getId());
        assert fetched != null;
        assert fetched.getName().equals("Product1");
    }

    @Test
    void testGetProductById_ProductDoesNotExist() {
        Product fetched = repository.getProductById(UUID.randomUUID());
        assert fetched == null;
    }

    // ---------- Mark As Sold ----------
    @Test
    void testMarkAsSold_ProductExistsAndNotSold() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        assert repository.getAvailableProducts().isEmpty();
    }

    @Test
    void testMarkAsSold_ProductExistsButAlreadySold() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        assertThrows(IllegalStateException.class, () -> repository.markAsSold(product.getId()));
    }

    @Test
    void testMarkAsSold_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.markAsSold(UUID.randomUUID()));
    }

    // ---------- Remove Product ----------
    @Test
    void testRemoveProduct_ProductExists() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.removeProduct("admin1", product.getId());
        assert repository.getAllProducts().isEmpty();
    }

    @Test
    void testRemoveProduct_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.removeProduct("admin1", UUID.randomUUID()));
    }

    @Test
    void testRemoveProduct_EmptyAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.removeProduct(" ", product.getId());
        });
        assertEquals("Username cannot be null or empty", exception.getMessage());
        assertFalse(repository.getAllProducts().isEmpty(), "Product should not be removed");
    }

    @Test
    void test_NotLoggedAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.removeProduct("admin2", product.getId());
        });
        assertEquals("Admin not logged in", exception.getMessage());
        assertFalse(repository.getAllProducts().isEmpty(), "Product should not be removed");
    }

    // ---------- Update Coupon Cost Price ----------
    @Test
    void testUpdateCostPrice_ProductExistsAndPriceValid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateCouponCostPrice("admin1", product.getId(), 50.0);
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getCostPrice() == 50.0;
    }

    @Test
    void testUpdateCostPrice_EmptyAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateCouponCostPrice(" ", product.getId(), 50.0);
        });
        assertEquals("Username cannot be null or empty", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateCostPrice_NotLoggedAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateCouponCostPrice("admin2", product.getId(), 50.0);
        });
        assertEquals("Admin not logged in", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateCostPrice_ProductExistsButPriceInvalid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateCouponCostPrice("admin1", product.getId(), -5.0));
    }

    @Test
    void testUpdateCostPrice_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateCouponCostPrice("admin1", UUID.randomUUID(), 50.0));
    }

    // ---------- Update Coupon Margin Percentage ----------
    @Test
    void testUpdateMarginPercentage_ProductExistsAndPercentageValid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateCouponMarginPercentage("admin1", product.getId(), 20.0);
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getMarginPercentage() == 20.0;
    }

    @Test
    void testUpdateMarginPercentage_EmptyAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateCouponMarginPercentage(" ", product.getId(), 20.0);
        });
        assertEquals("Username cannot be null or empty", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateMarginPercentage_NotLoggedAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateCouponMarginPercentage("admin2", product.getId(), 20.0);
        });
        assertEquals("Admin not logged in", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateMarginPercentage_ProductExistsButPercentageInvalid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateCouponMarginPercentage("admin1", product.getId(), -5.0));
    }

    @Test
    void testUpdateMarginPercentage_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateCouponMarginPercentage("admin1", UUID.randomUUID(), 20.0));
    }

    // ---------- Update Coupon Value ----------
    @Test
    void testUpdateValue_ProductExistsAndValueValid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateCouponValue("admin1", product.getId(), Coupon.ValueType.STRING, "Updated Value");
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getValue().equals("Updated Value");
    }

    @Test
    void testUpdateValue_EmptyAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateCouponValue(" ", product.getId(), Coupon.ValueType.STRING, "Updated Value");
        });
        assertEquals("Username cannot be null or empty", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateValue_NotLoggedAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateCouponValue("admin2", product.getId(), Coupon.ValueType.STRING, "Updated Value");
        });
        assertEquals("Admin not logged in", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateValue_ProductExistsButValueInvalid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateCouponValue("admin1", product.getId(), null, null));
    }

    @Test
    void testUpdateValue_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateCouponValue("admin1", UUID.randomUUID(), ValueType.STRING, "Updated Value"));
    }

    // ---------- Update Image URL ----------
    @Test
    void testUpdateImageURL_ProductExistsAndURLValid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateImageURL("admin1", product.getId(), "http://new.image.url/1");
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getImageUrl().equals("http://new.image.url/1");
    }

     @Test
    void testUpdateImageURL_EmptyAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateImageURL(" ", product.getId(), "http://new.image.url/1");
        });
        assertEquals("Username cannot be null or empty", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateImageURL_NotLoggedAdmin() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = repository.getAllProducts().get(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.updateImageURL("admin2", product.getId(), "http://new.image.url/1");
        });
        assertEquals("Admin not logged in", exception.getMessage());
        Product updatedProduct = repository.getProductById(product.getId());
        assertEquals(10.0, updatedProduct.getCostPrice(), "Cost price should not have changed");
    }

    @Test
    void testUpdateImageURL_ProductExistsButURLInvalid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class, () -> repository.updateImageURL("admin1", product.getId(), null));
    }

    @Test
    void testUpdateImageURL_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateImageURL("admin1", UUID.randomUUID(), "http://new.image.url/1"));
    }

    // ---------- Purchase Product By Customer ----------
    @Test
    void testPurchaseProductByCustomer_ProductExistsAndNotSold() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        String value = repository.purchaseProductByCustomer(product.getId());
        assert value != null;
    }

    @Test
    void testPurchaseProductByCustomer_concurrentAccess() throws InterruptedException, ExecutionException {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        UUID productId = repository.getAllProducts().get(0).getId();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(executor.submit(() -> {
                try {
                    return repository.purchaseProductByCustomer(productId);
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
            if ("Value1".equals(result)) {
                successCount++;
            } else if ("Product is already sold".equals(result)) {
                soldExceptionCount++;
            }
        }
        assertEquals(1, successCount, "Only one thread should succeed in purchasing");
        assertEquals(4, soldExceptionCount, "All other threads should fail because product is sold");
    }

    @Test
    void testPurchaseProductByCustomer_ProductExistsAndSold() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        assertThrows(IllegalStateException.class, () -> repository.purchaseProductByCustomer(product.getId()));
    }

    @Test
    void testPurchaseProductByCustomer_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.purchaseProductByCustomer(UUID.randomUUID()));
    }

    // ---------- Purchase Product By Reseller ----------
    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldAndPriceValid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        double price = repository.purchaseProductByReseller(product.getId(), 100.0);
        assert price == 100.0;
        ;
    }

    @Test
    void testPurchaseProductByReseller_concurrentAccess() throws InterruptedException, ExecutionException {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        UUID productId = repository.getAllProducts().get(0).getId();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Double>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(executor.submit(() -> {
                try {
                    double price=repository.purchaseProductByReseller(productId, 15.0);
                    return price;
                } catch (IllegalStateException | IllegalArgumentException e) {
                    return null;
                }
            }));
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        int successCount = 0;
        int soldExceptionCount = 0;
        for (Future<Double> f : futures) {
            Double result = f.get();
            if (result==null)
                soldExceptionCount++; 
            else if (result == 15.0) {
                successCount++;
            } 
        }
        assertEquals(1, successCount, "Only one thread should succeed in purchasing");
        assertEquals(4, soldExceptionCount, "Other threads should fail because product is already sold");
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldButPriceInvalid() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class, () -> repository.purchaseProductByReseller(product.getId(), -5.0));
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndSold() {
        UUID id = UUID.randomUUID();
        repository.addCoupon(id,"admin1", "Product1", "Description1", "http://image.url/1", 10.0, 20.0,
                Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        assertThrows(IllegalStateException.class, () -> repository.purchaseProductByReseller(product.getId(), 100.0));
    }

    @Test
    void testPurchaseProductByReseller_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.purchaseProductByReseller(UUID.randomUUID(), 100.0));
    }

}
