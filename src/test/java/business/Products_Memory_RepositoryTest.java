package business;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class Products_Memory_RepositoryTest {
    private Products_Memory_Repository repository;

    @BeforeEach
    void setUp() {
        repository = new Products_Memory_Repository();
    }

    @AfterEach
    void tearDown() {
        repository = null;
    }

    @Test
    void testAddCoupon_Success() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        assert repository.getAllProducts().size() == 1;
    }

    // ---------- Name validations ----------
    @Test
    void testAddCoupon_nullName_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon(null,
                "Valid description", "http://image.jpg", 10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Coupon name cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_emptyName_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("  ",
                "Valid description", "http://image.jpg", 10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Coupon name cannot be null or empty", ex.getMessage());
    }

    // ---------- Description validations ----------
    @Test
    void testAddCoupon_nullDescription_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                null, "http://image.jpg", 10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_emptyDescription_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                " ", "http://image.jpg", 10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
    }

    // ---------- Image URL validations ----------
    @Test
    void testAddCoupon_nullImageUrl_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                "Description", null, 10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Image URL cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_invalidImageUrl_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                "Description", "ftp://image.jpg", 10.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Image URL must start with http:// or https://", ex.getMessage());
    }

    // ---------- Cost price validations ----------
    @Test
    void testAddCoupon_negativeCostPrice_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                "Description", "http://image.jpg", -5.0, 20.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Cost price cannot be negative", ex.getMessage());
    }

    // ---------- Margin percentage validations ----------
    @Test
    void testAddCoupon_invalidMarginPercentageBelowZero_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                "Description", "http://image.jpg", 10.0, -5.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
    }

    @Test
    void testAddCoupon_invalidMarginPercentageAboveHundred_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                "Description", "http://image.jpg", 10.0, 120.0, Coupon.ValueType.STRING, "10$ OFF"));
        assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
    }

    // ---------- Value type validations ----------
    @Test
    void testAddCoupon_nullValueType_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repository.addCoupon("Coupon", "Description", "http://image.jpg", 10.0, 20.0, null, "10$ OFF"));
        assertEquals("Value type cannot be null", ex.getMessage());
    }

    // ---------- Value validations ----------
    @Test
    void testAddCoupon_nullValue_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                "Description", "http://image.jpg", 10.0, 20.0, Coupon.ValueType.STRING, null));
        assertEquals("Coupon value cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_emptyValue_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> repository.addCoupon("Coupon",
                "Description", "http://image.jpg", 10.0, 20.0, Coupon.ValueType.STRING, "  "));
        assertEquals("Coupon value cannot be null or empty", ex.getMessage());
    }

    @Test
    void testGetAvailableProducts_AvailableExists() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        repository.addCoupon("Product2", "Description2", "http://image.url/2", 15.0, 25.0, Coupon.ValueType.STRING,
                "Value2");
        repository.markAsSold(repository.getAllProducts().get(0).getId());
        assert repository.getAvailableProducts().size() == 1;
    }

    @Test
    void testGetAvailableProducts_AllSold() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        repository.addCoupon("Product2", "Description2", "http://image.url/2", 15.0, 25.0, Coupon.ValueType.STRING,
                "Value2");
        repository.getAllProducts().forEach(p -> repository.markAsSold(p.getId()));
        assertTrue(repository.getAvailableProducts().isEmpty(), "No products should be available when all are sold");
    }

    @Test
    void testGetProductById_ProductExists() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
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

    @Test
    void testMarkAsSold_ProductExistsAndNotSold() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        assert repository.getAvailableProducts().isEmpty();
    }

    @Test
    void testMarkAsSold_ProductExistsButAlreadySold() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        assertThrows(IllegalStateException.class, () -> repository.markAsSold(product.getId()));
    }

    @Test
    void testMarkAsSold_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.markAsSold(UUID.randomUUID()));
    }

    @Test
    void testRemoveProduct_ProductExists() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.removeProduct(product.getId());
        assert repository.getAllProducts().isEmpty();
    }

    @Test
    void testRemoveProduct_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.removeProduct(UUID.randomUUID()));
    }

    @Test
    void testUpdateCostPrice_ProductExistsAndPriceValid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateCostPrice(product.getId(), 50.0);
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getCostPrice() == 50.0;
    }

    @Test
    void testUpdateCostPrice_ProductExistsButPriceInvalid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class, () -> repository.updateCostPrice(product.getId(), -5.0));
    }

    @Test
    void testUpdateCostPrice_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.updateCostPrice(UUID.randomUUID(), 50.0));
    }

    @Test
    void testUpdateImageURL_ProductExistsAndURLValid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateImageURL(product.getId(), "http://new.image.url/1");
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getImageUrl().equals("http://new.image.url/1");
    }

    @Test
    void testUpdateImageURL_ProductExistsButURLInvalid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class, () -> repository.updateImageURL(product.getId(), null));
    }

    @Test
    void testUpdateImageURL_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateImageURL(UUID.randomUUID(), "http://new.image.url/1"));
    }

    @Test
    void testUpdateMarginPercentage_ProductExistsAndPercentageValid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateMarginPercentage(product.getId(), 20.0);
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getMarginPercentage() == 20.0;
    }

    @Test
    void testUpdateMarginPercentage_ProductExistsButPercentageInvalid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class, () -> repository.updateMarginPercentage(product.getId(), -5.0));
    }

    @Test
    void testUpdateMarginPercentage_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.updateMarginPercentage(UUID.randomUUID(), 20.0));
    }

    @Test
    void testUpdateValue_ProductExistsAndValueValid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.updateValue(product.getId(), "Updated Value");
        Product updatedProduct = repository.getProductById(product.getId());
        assert updatedProduct.getValue().equals("Updated Value");
    }

    @Test
    void testUpdateValue_ProductExistsButValueInvalid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class, () -> repository.updateValue(product.getId(), null));
    }

    @Test
    void testUpdateValue_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.updateValue(UUID.randomUUID(), "Updated Value"));
    }

    @Test
    void testPurchaseProductByCustomer_ProductExistsAndNotSold() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        String value = repository.purchaseProductByCustomer(product.getId());
        assert value != null;
    }

    @Test
    void testPurchaseProductByCustomer_concurrentAccess() throws InterruptedException, ExecutionException {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
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
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        repository.markAsSold(product.getId());
        assertThrows(IllegalStateException.class, () -> repository.purchaseProductByCustomer(product.getId()));
    }

    @Test
    void testPurchaseProductByCustomer_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> repository.purchaseProductByCustomer(UUID.randomUUID()));
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldAndPriceValid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        String value = repository.purchaseProductByReseller(product.getId(), 100.0);
        assert value != null;
    }

    @Test
    void testPurchaseProductByReseller_concurrentAccess() throws InterruptedException, ExecutionException {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        UUID productId = repository.getAllProducts().get(0).getId();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(executor.submit(() -> {
                try {
                    return repository.purchaseProductByReseller(productId, 15.0);
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
            if ("Value1".equals(result))
                successCount++;
            else if ("Product is already sold".equals(result))
                soldExceptionCount++;
        }
        assertEquals(1, successCount, "Only one thread should succeed in purchasing");
        assertEquals(4, soldExceptionCount, "Other threads should fail because product is already sold");
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndNotSoldButPriceInvalid() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
                "Value1");
        Product product = repository.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class, () -> repository.purchaseProductByReseller(product.getId(), -5.0));
    }

    @Test
    void testPurchaseProductByReseller_ProductExistsAndSold() {
        repository.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, Coupon.ValueType.STRING,
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
