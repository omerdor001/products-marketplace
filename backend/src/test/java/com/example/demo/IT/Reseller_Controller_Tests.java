package com.example.demo.IT;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.demo.controllers.ResellerController;
import com.example.demo.domain.Coupon;
import com.example.demo.domain.Coupon.ValueType;
import com.example.demo.domain.Product;
import com.example.demo.security.JWTTokenValidator;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;

@WebMvcTest(ResellerController.class)
public class Reseller_Controller_Tests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private PurchaseService purchaseService;

    private String generateToken() {
        return JWTTokenValidator
                .getInstance("testsecretkeytestsecretkeytestsecretkey", 3600000)
                .generateTokenForTesting("reseller1");
    }

    @Test
    void getAvailableProducts_ShouldReturnProducts() throws Exception {
        String token = generateToken();
        List<Product> mockProducts = Arrays.asList(
                new Coupon("Name", "Desc", "http://img.com", 10.0, 20.0, ValueType.STRING, "100"),
                new Coupon("Name 2", "Desc 2", "http://img2.com", 15.0, 25.0, ValueType.STRING, "101")
        );
        when(productService.getAvailableProducts(token)).thenReturn(mockProducts);
        mockMvc.perform(get("/api/v1/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[1].costPrice").value(15.0));
        verify(productService).getAvailableProducts(token);
    }

    @Test
    void getAvailableProducts_ShouldReturnUnauthorized_WhenInvalidToken() throws Exception {
        String invalidToken = "bad-token";
        when(productService.getAvailableProducts(invalidToken))
                .thenThrow(new SecurityException());
        mockMvc.perform(get("/api/v1/products")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"));
    }

    @Test
    void getAvailableProducts_ShouldReturnInternalServerError_WhenException() throws Exception {
        String token = generateToken();
        when(productService.getAvailableProducts(token))
                .thenThrow(new RuntimeException("DB failure"));
        mockMvc.perform(get("/api/v1/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error_code").value("INTERNAL_ERROR"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        String token = generateToken();
        UUID productId = UUID.randomUUID();
        Product product = new Coupon(
                "Name", "Desc", "http://img.com",
                10.0, 20.0, ValueType.STRING, "100"
        );
        when(productService.getProductById(productId, token)).thenReturn(product);
        mockMvc.perform(get("/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.costPrice").value(10.0));
        verify(productService).getProductById(productId, token);
    }

    @Test
    void getProductById_ShouldReturnUnauthorized_WhenInvalidToken() throws Exception {
        UUID productId = UUID.randomUUID();
        String invalidToken = "bad-token";
        when(productService.getProductById(productId, invalidToken))
                .thenThrow(new SecurityException());
        mockMvc.perform(get("/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"));
    }

    @Test
    void getProductById_ShouldReturn404_WhenNotFound() throws Exception {
        String token = generateToken();
        UUID productId = UUID.randomUUID();
        when(productService.getProductById(productId, token))
                .thenThrow(new NoSuchElementException());
        mockMvc.perform(get("/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    void purchaseProduct_ShouldReturnSuccess() throws Exception {
        String token = generateToken();
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, token))
                .thenReturn(100.0);
        mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reseller_price\":50.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").exists())
                .andExpect(jsonPath("$.final_price").value(50.0));
        verify(purchaseService).purchaseProductByReseller(productId, 50.0, token);
    }

    @Test
    void purchaseProduct_ShouldReturn404_WhenNotFound() throws Exception {
        String token = generateToken();
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, token))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reseller_price\":50.0}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    void purchaseProduct_ShouldReturn409_WhenAlreadySold() throws Exception {
        String token = generateToken();
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, token))
                .thenThrow(new IllegalStateException());
        mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reseller_price\":50.0}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error_code").value("PRODUCT_ALREADY_SOLD"));
    }

    @Test
    void purchaseProduct_ShouldReturn400_WhenPriceTooLow() throws Exception {
        String token = generateToken();
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, token))
                .thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reseller_price\":50.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("RESELLER_PRICE_TOO_LOW"));
    }

    @Test
    void purchaseProduct_ShouldReturn401_WhenUnauthorized() throws Exception {
        String token = "bad-token";
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, token))
                .thenThrow(new SecurityException());
        mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reseller_price\":50.0}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"));
    }

    @Test
    void purchaseProduct_ShouldReturn500_WhenOtherException() throws Exception {
        String token = generateToken();
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, token))
                .thenThrow(new RuntimeException("DB failure"));
        mockMvc.perform(post("/api/v1/products/" + productId + "/purchase")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reseller_price\":50.0}"))

                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error_code").value("INTERNAL_ERROR"));
    }
}