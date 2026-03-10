package com.example.demo.IT;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    void getAvailableProducts_ShouldReturnProducts() throws Exception { 
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        List<Product> mockProducts = Arrays.asList(
                new Coupon("Name", "Desc", "http://img.com", 10.0, 20.0, ValueType.STRING, "100"),
                new Coupon("Name 2", "Desc 2", "http://img2.com", 15.0, 25.0, ValueType.STRING, "101"));
        when(productService.getAvailableProducts(validJwtToken)).thenReturn(mockProducts);
        mockMvc.perform(get("/api/v1/products").param("token", validJwtToken) 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[1].costPrice").value(15.0));
        verify(productService).getAvailableProducts(validJwtToken);
    }

    @Test
    void getAvailableProducts_ShouldReturnUnauthorized_WhenInvalidToken() throws Exception {
        String invalidToken = "bad-token";
        when(productService.getAvailableProducts(invalidToken))
                .thenThrow(new SecurityException("Invalid token"));

        mockMvc.perform(get("/api/v1/products")
                .param("token", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Unauthorized purchase attempt"));
    }

    @Test
    void getAvailableProducts_ShouldReturnInternalServerError_WhenException() throws Exception { 
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        when(productService.getAvailableProducts(validJwtToken)).thenThrow(new RuntimeException("DB failure"));
        mockMvc.perform(get("/api/v1/products").param("token", validJwtToken) 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception { 
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        UUID productId = UUID.randomUUID();
        Product product = new Coupon("Name", "Desc", "http://img.com", 10.0, 20.0, ValueType.STRING, "100");
        when(productService.getProductById(productId,validJwtToken)).thenReturn(product);
        mockMvc.perform(get("/api/v1/products/" + productId).param("token", validJwtToken) 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.costPrice").value(10.0));
        verify(productService).getProductById(productId,validJwtToken);
    }

     @Test
    void getProductById_ShouldReturnUnauthorized_WhenInvalidToken() throws Exception { 
        UUID productId = UUID.randomUUID();
        String invalidToken = "bad-token";
        when(productService.getProductById(productId, invalidToken))
                .thenThrow(new SecurityException("Invalid token"));
        mockMvc.perform(get("/api/v1/products/{productId}", productId)
                .param("token", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Unauthorized purchase attempt"));
    }

    @Test
    void getProductById_ShouldReturn404_WhenNotFound() throws Exception { 
        String validJwtToken = JWTTokenValidator.getInstance("testsecretkeytestsecretkeytestsecretkey", 3600000).generateTokenForTesting("reseller1");
        UUID productId = UUID.randomUUID();
        when(productService.getProductById(productId,validJwtToken)).thenThrow(new NoSuchElementException());
        mockMvc.perform(get("/api/v1/products/" + productId).param("token", validJwtToken) 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void purchaseProduct_ShouldReturnPrice() throws Exception {
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, "token123")).thenReturn(55.0);
        mockMvc.perform(get("/api/v1/" + productId + "/purchase/50.0/token123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("55.0"));
        verify(purchaseService).purchaseProductByReseller(productId, 50.0, "token123");
    }

    @Test
    void purchaseProduct_ShouldReturn404_WhenNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, "token123"))
                .thenThrow(new NoSuchElementException());
        mockMvc.perform(get("/api/v1/" + productId + "/purchase/50.0/token123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    void purchaseProduct_ShouldReturn409_WhenAlreadySold() throws Exception {
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, "token123"))
                .thenThrow(new IllegalStateException());
        mockMvc.perform(get("/api/v1/" + productId + "/purchase/50.0/token123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error_code").value("PRODUCT_ALREADY_SOLD"));
    }

    @Test
    void purchaseProduct_ShouldReturn400_WhenPriceTooLow() throws Exception {
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, "token123"))
                .thenThrow(new IllegalArgumentException());
        mockMvc.perform(get("/api/v1/" + productId + "/purchase/50.0/token123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("RESELLER_PRICE_TOO_LOW"));
    }

    @Test
    void purchaseProduct_ShouldReturn401_WhenUnauthorized() throws Exception {
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, "token123"))
                .thenThrow(new SecurityException());
        mockMvc.perform(get("/api/v1/" + productId + "/purchase/50.0/token123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"));
    }

    @Test
    void purchaseProduct_ShouldReturn500_WhenOtherException() throws Exception {
        UUID productId = UUID.randomUUID();
        when(purchaseService.purchaseProductByReseller(productId, 50.0, "token123"))
                .thenThrow(new RuntimeException("DB failure"));

        mockMvc.perform(get("/api/v1/" + productId + "/purchase/50.0/token123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error_code").value("INTERNAL_ERROR"));
    }

}
