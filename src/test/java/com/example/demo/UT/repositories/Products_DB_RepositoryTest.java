package com.example.demo.UT.repositories;

import com.example.demo.data_access.JpaCouponRepository;
import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.domain.Coupon.ValueType;
import com.example.demo.repositories.Products_DB_Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Products_DB_RepositoryTest {

    @InjectMocks
    private Products_DB_Repository repository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private JpaCouponRepository dbRepository;

    private final String admin = "admin1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new Products_DB_Repository(dbRepository);
        repository.setEntityManager(entityManager);
    }

    // ---------- Add ----------
    @Test
    void testAddCoupon() {
        repository.addCoupon(admin, "Name", "Desc", "http://img.com", 10.0, 20.0, ValueType.STRING, "100");
        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(entityManager).persist(captor.capture());
        Coupon persisted = captor.getValue();
        assertEquals("Name", persisted.getName());
        assertEquals(10.0, persisted.getCostPrice());
        assertEquals(20.0, persisted.getMarginPercentage());
        assertEquals(ValueType.STRING, persisted.getValueType());
    }

    @Test
    void testSaveProduct() {
        Product product = mock(Product.class);
        when(entityManager.merge(product)).thenReturn(product);
        Product result = repository.saveProduct(product);
        assertSame(product, result);
        verify(entityManager).merge(product);
    }

    // ---------- Retrieve ----------
    @Test
    void testGetProductByIdFound() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        Product result = repository.getProductById(id);
        assertSame(product, result);
    }

    @Test
    void testGetProductByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(Product.class, id)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> repository.getProductById(id));
    }

    @Test
    void testGetAllProducts() {
        TypedQuery<Product> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM Product p", Product.class)).thenReturn(query);
        List<Product> products = List.of(mock(Product.class), mock(Product.class));
        when(query.getResultList()).thenReturn(products);
        List<Product> result = repository.getAllProducts();
        assertEquals(products, result);
    }

    // ---------- Update ----------
    @Test
    void testUpdateCouponCostPrice() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        repository.updateCouponCostPrice(admin, id, 50.0);
        verify(product).setCostPrice(50.0);
    }

    @Test
    void testUpdateCouponCostPriceNotFound() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(Product.class, id)).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateCouponCostPrice(admin, id, 50.0));
    }

    @Test
    void testUpdateCouponMarginPercentage() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        repository.updateCouponMarginPercentage(admin, id, 30);
        verify(product).setMarginPercentage(30);
    }

    @Test
    void testUpdateCouponValue() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        repository.updateCouponValue(admin, id, ValueType.STRING, "25");
        verify(product).setValueType(ValueType.STRING);
        verify(product).setValue("25");
    }

    @Test
    void testUpdateImageURL() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        repository.updateImageURL(admin, id, "http://example.com/image.jpg");
        verify(product).setImageUrl("http://example.com/image.jpg");
    }

    // ---------- Mark as Sold ----------
    @Test
    void testMarkAsSold() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        repository.markAsSold(id);
        verify(product).setSold(true);
    }

    @Test
    void testMarkAsSoldNotFound() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(Product.class, id)).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> repository.markAsSold(id));
    }

    // ---------- Delete ----------
    @Test
    void testRemoveProduct() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        repository.removeProduct(admin, id);
        verify(entityManager).remove(product);
    }

    @Test
    void testRemoveProductNotFound() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(Product.class, id)).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> repository.removeProduct(admin, id));
    }

    // ---------- Purchase ----------
    @Test
    void testPurchaseProductByCustomerSuccess() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        when(product.isSold()).thenReturn(false);
        when(product.getValue()).thenReturn("VALUE");
        String result = repository.purchaseProductByCustomer(id);
        assertEquals("VALUE", result);
        verify(product).setSold(true);
    }

    @Test
    void testPurchaseProductByCustomerAlreadySold() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        when(product.isSold()).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> repository.purchaseProductByCustomer(id));
    }

    @Test
    void testPurchaseProductByResellerSuccess() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        when(product.getCostPrice()).thenReturn(100.0);
        double result = repository.purchaseProductByReseller(id, 150.0);
        assertEquals(150.0, result);
        verify(product).setSold(true);
    }

    @Test
    void testPurchaseProductByResellerTooLowPrice() {
        UUID id = UUID.randomUUID();
        Product product = mock(Product.class);
        when(entityManager.find(Product.class, id)).thenReturn(product);
        when(product.getCostPrice()).thenReturn(100.0);
        assertThrows(IllegalArgumentException.class,
                () -> repository.purchaseProductByReseller(id, 50.0));
    }
}