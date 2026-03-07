package service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void testAddCoupon() {
        productService.addCoupon("Product1", "Description1", "http://image.url/1", 10.0, 20.0, domain.Coupon.ValueType.STRING,
                "Value1");
        assertEquals(1, productService.getAllProducts().size());
        assertEquals("Product1", productService.getAllProducts().get(0).getName
());
    }

    @Test
    void testGetAvailableProducts() {

    }

    @Test
    void testGetProductById() {

    }

    @Test
    void testRemoveProduct() {

    }

    @Test
    void testUpdateCouponCostPrice() {

    }

    @Test
    void testUpdateCouponMarginPercentage() {

    }

    @Test
    void testUpdateCouponValue() {

    }

    @Test
    void testUpdateImageURL() {

    }
}
