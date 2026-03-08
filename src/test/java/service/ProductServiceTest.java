package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;

public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = ProductService.getInstance();
    }

    @AfterEach
    void tearDown() {
        ProductService.resetInstance();
    }

    // ---------- Add Coupon ----------
    @Test
    void testAddCoupon_Success() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");

        List<Product> products = productService.getAllProducts();
        assertEquals(1, products.size());
        Product p = products.get(0);
        assertEquals("Product1", p.getName());
        assertEquals("Description1", p.getDescription());
        assertEquals(10.0, p.getCostPrice());
        assertEquals(20.0, p.getMarginPercentage());
        assertEquals("Value1", p.getValue());
    }

    @Test
    void testAddCoupon_NullName_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon(null, "Description", "http://image.url/1", 10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon name cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_EmptyName_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("  ", "Description", "http://image.url/1", 10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon name cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_NullDescription_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", null, "http://image.url/1", 10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_EmptyDescription_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", " ", "http://image.url/1", 10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Coupon description cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_NullImageUrl_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", null, 10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Image URL cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_InvalidImageUrl_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", "ftp://image.jpg", 10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Image URL must start with http:// or https://", ex.getMessage());
    }

    @Test
    void testAddCoupon_NegativeCostPrice_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", "http://image.url/1", -10.0, 20.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Cost price cannot be negative", ex.getMessage());
    }

    @Test
    void testAddCoupon_MarginBelowZero_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", "http://image.url/1", 10.0, -5.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
    }

    @Test
    void testAddCoupon_MarginAboveHundred_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", "http://image.url/1", 10.0, 120.0,
                        Coupon.ValueType.STRING, "Value1"));
        assertEquals("Margin percentage must be between 0 and 100", ex.getMessage());
    }

    @Test
    void testAddCoupon_NullValueType_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", "http://image.url/1", 10.0, 20.0,
                        null, "Value1"));
        assertEquals("Value type cannot be null", ex.getMessage());
    }

    @Test
    void testAddCoupon_NullValue_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", "http://image.url/1", 10.0, 20.0,
                        Coupon.ValueType.STRING, null));
        assertEquals("Coupon value cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddCoupon_EmptyValue_ShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.addCoupon("Product", "Description", "http://image.url/1", 10.0, 20.0,
                        Coupon.ValueType.STRING, "  "));
        assertEquals("Coupon value cannot be null or empty", ex.getMessage());
    }

    // ---------- Get Available Products ----------

    @Test
    void testGetAvailableProducts_AvailableExists() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        productService.addCoupon("Product2", "Description2", "http://image.url/2",
                15.0, 25.0, Coupon.ValueType.STRING, "Value2");
        Product first = productService.getAllProducts().get(0);
        productService.markAsSold(first.getId());
        List<Product> available = productService.getAvailableProducts();
        assertEquals(1, available.size());
        assertEquals("Product2", available.get(0).getName());
    }

    @Test
    void testGetAvailableProducts_AllSold() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        productService.addCoupon("Product2", "Description2", "http://image.url/2",
                15.0, 25.0, Coupon.ValueType.STRING, "Value2");
        productService.getAllProducts().forEach(p -> productService.markAsSold(p.getId()));
        List<Product> available = productService.getAvailableProducts();
        assertTrue(available.isEmpty(),
                "No products should be available when all are sold");
    }

    // ---------- Get Product by ID ----------

    @Test
    void testGetProductById_ProductExists() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = productService.getAllProducts().get(0);
        Product fetched = productService.getProductById(product.getId());
        assertNotNull(fetched);
        assertEquals("Product1", fetched.getName());
    }

    @Test
    void testGetProductById_ProductDoesNotExist() {
        Product fetched = productService.getProductById(UUID.randomUUID());
        assertNull(fetched);
    }

    // ---------- Remove Product ----------

    @Test
    void testRemoveProduct_ProductExists() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = productService.getAllProducts().get(0);
        productService.removeProduct(product.getId());
        assertTrue(productService.getAllProducts().isEmpty());
    }

    @Test
    void testRemoveProduct_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.removeProduct(UUID.randomUUID()));
    }

    // ---------- Update Coupon Cost Price ----------

    @Test
    void testUpdateCouponCostPrice_ProductExistsAndPriceValid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = productService.getAllProducts().get(0);
        productService.updateCouponCostPrice(product.getId(), 50.0);
        Product updatedProduct = productService.getProductById(product.getId());
        assertEquals(50.0, updatedProduct.getCostPrice());
    }

    @Test
    void testUpdateCouponCostPrice_ProductExistsButPriceInvalid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");
        Product product = productService.getAllProducts().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponCostPrice(product.getId(), -5.0));
    }

    @Test
    void testUpdateCouponCostPrice_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponCostPrice(UUID.randomUUID(), 50.0));
    }

    // ---------- Update Coupon Margin Percentage ----------

    @Test
    void testUpdateCouponMarginPercentage_ProductExistsAndPercentageValid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");

        Product product = productService.getAllProducts().get(0);

        productService.updateCouponMarginPercentage(product.getId(), 30.0);

        Product updatedProduct = productService.getProductById(product.getId());

        assertEquals(30.0, updatedProduct.getMarginPercentage());
    }

    @Test
    void testUpdateCouponMarginPercentage_ProductExistsButPercentageInvalid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");

        Product product = productService.getAllProducts().get(0);

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponMarginPercentage(product.getId(), -5.0));
    }

    @Test
    void testUpdateCouponMarginPercentage_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponMarginPercentage(UUID.randomUUID(), 20.0));
    }

    // ---------- Update Coupon Value ----------

    @Test
    void testUpdateCouponValue_ProductExistsAndValueValid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");

        Product product = productService.getAllProducts().get(0);

        productService.updateCouponValue(product.getId(), Coupon.ValueType.STRING, "Updated Value");

        Product updatedProduct = productService.getProductById(product.getId());

        assertEquals("Updated Value", updatedProduct.getValue());
    }

    @Test
    void testUpdateCouponValue_ProductExistsButValueInvalid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");

        Product product = productService.getAllProducts().get(0);

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponValue(product.getId(), null, null));
    }

    @Test
    void testUpdateCouponValue_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.updateCouponValue(UUID.randomUUID(),
                        Coupon.ValueType.STRING, "Updated Value"));
    }

    // ---------- Update Image URL----------

    @Test
    void testUpdateImageURL_ProductExistsAndURLValid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");

        Product product = productService.getAllProducts().get(0);

        productService.updateImageURL(product.getId(), "http://new.image.url/1");

        Product updatedProduct = productService.getProductById(product.getId());

        assertEquals("http://new.image.url/1", updatedProduct.getImageUrl());
    }

    @Test
    void testUpdateImageURL_ProductExistsButURLInvalid() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1",
                10.0, 20.0, Coupon.ValueType.STRING, "Value1");

        Product product = productService.getAllProducts().get(0);

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateImageURL(product.getId(), null));
    }

    @Test
    void testUpdateImageURL_ProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.updateImageURL(UUID.randomUUID(), "http://new.image.url/1"));
    }
}
