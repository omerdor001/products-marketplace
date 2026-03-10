package com.example.demo.IT;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.controllers.ProductController;
import com.example.demo.domain.Coupon;
import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;

@WebMvcTest(ProductController.class)
public class Product_Controller_Tests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void addCoupon_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/products/coupon")
                        .param("username", "admin")
                        .param("name", "Super Coupon")
                        .param("description", "Discount 20%")
                        .param("imageUrl", "http://img.com/coupon.png")
                        .param("costPrice", "10.0")
                        .param("marginPercentage", "25.0")
                        .param("valueType", "STRING")
                        .param("value", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon added successfully"));
        verify(productService).addCoupon(
                "admin", "Super Coupon", "Discount 20%", "http://img.com/coupon.png",
                10.0, 25.0, Coupon.ValueType.STRING, "100"
        );
    }

    @Test
    void addCoupon_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        doThrow(new RuntimeException("Failed to add coupon"))
                .when(productService).addCoupon(
                        "admin", "Super Coupon", "Discount 20%", "http://img.com/coupon.png",
                        10.0, 25.0, Coupon.ValueType.STRING, "100"
                );
        mockMvc.perform(post("/products/coupon")
                        .param("username", "admin")
                        .param("name", "Super Coupon")
                        .param("description", "Discount 20%")
                        .param("imageUrl", "http://img.com/coupon.png")
                        .param("costPrice", "10.0")
                        .param("marginPercentage", "25.0")
                        .param("valueType", "STRING")
                        .param("value", "100"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to add coupon"));
    }

    @Test
    void removeProduct_ShouldReturnOk() throws Exception {
        UUID productId = UUID.randomUUID();
        mockMvc.perform(delete("/products/" + productId)
                .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product removed successfully"));
        verify(productService).removeProduct("admin", productId);
    }

    @Test
    void removeProduct_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new RuntimeException("Failed to remove"))
                .when(productService).removeProduct("admin", productId);
        mockMvc.perform(delete("/products/" + productId)
                .param("username", "admin"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to remove"));
    }

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        List<Product> mockProducts = Arrays.asList(
                new Coupon("Name1", "Desc1", "http://img1.com", 10.0, 20.0, Coupon.ValueType.STRING, "100"),
                new Coupon("Name2", "Desc2", "http://img2.com", 15.0, 25.0, Coupon.ValueType.STRING, "101")
        );
        when(productService.getAllProducts()).thenReturn(mockProducts);
        mockMvc.perform(get("/products/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Name1"))
                .andExpect(jsonPath("$[1].description").value("Desc2"));
        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        when(productService.getAllProducts()).thenThrow(new RuntimeException("DB failure"));
        mockMvc.perform(get("/products/all"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAvailableProducts_ShouldReturnProducts() throws Exception {
        List<Product> mockProducts = Arrays.asList(
                new Coupon("Name1", "Desc1", "http://img1.com", 10.0, 20.0, Coupon.ValueType.STRING, "100")
        );
        when(productService.getAvailableProducts()).thenReturn(mockProducts);
        mockMvc.perform(get("/products/availables")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Name1"));
        verify(productService).getAvailableProducts();
    }

    @Test
    void getAvailableProducts_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        when(productService.getAvailableProducts()).thenThrow(new RuntimeException("Service down"));
        mockMvc.perform(get("/products/availables"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateCouponCostPrice_ShouldReturnOk() throws Exception {
        UUID productId = UUID.randomUUID();
        mockMvc.perform(put("/products/" + productId + "/cost-price")
                .param("username", "admin")
                .param("costPrice", "50.0"))
                .andExpect(status().isOk());
        verify(productService).updateCouponCostPrice("admin", productId, 50.0);
    }

    @Test
    void updateCouponCostPrice_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new RuntimeException("Failed"))
                .when(productService).updateCouponCostPrice("admin", productId, 50.0);
        mockMvc.perform(put("/products/" + productId + "/cost-price")
                .param("username", "admin")
                .param("costPrice", "50.0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateCouponMarginPercentage_ShouldReturnOk() throws Exception {
        UUID productId = UUID.randomUUID();
        mockMvc.perform(put("/products/" + productId + "/margin")
                .param("username", "admin")
                .param("marginPercentage", "25.0"))
                .andExpect(status().isOk());
        verify(productService).updateCouponMarginPercentage("admin", productId, 25.0);
    }

    @Test
    void updateCouponMarginPercentage_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new RuntimeException("Failed"))
                .when(productService).updateCouponMarginPercentage("admin", productId, 25.0);
        mockMvc.perform(put("/products/" + productId + "/margin")
                .param("username", "admin")
                .param("marginPercentage", "25.0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateCouponValue_ShouldReturnOk() throws Exception {
        UUID productId = UUID.randomUUID();
        mockMvc.perform(put("/products/" + productId + "/value")
                .param("username", "admin")
                .param("valueType", "STRING")
                .param("value", "200"))
                .andExpect(status().isOk());
        verify(productService).updateCouponValue("admin", productId, Coupon.ValueType.STRING, "200");
    }

    @Test
    void updateCouponValue_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new RuntimeException("Failed"))
                .when(productService).updateCouponValue("admin", productId, Coupon.ValueType.STRING, "200");
        mockMvc.perform(put("/products/" + productId + "/value")
                .param("username", "admin")
                .param("valueType", "STRING")
                .param("value", "200"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateImageURL_ShouldReturnOk() throws Exception {
        UUID productId = UUID.randomUUID();
        mockMvc.perform(put("/products/" + productId + "/image-url")
                .param("username", "admin")
                .param("imageUrl", "http://newimage.com"))
                .andExpect(status().isOk());
        verify(productService).updateImageURL("admin", productId, "http://newimage.com");
    }

    @Test
    void updateImageURL_ShouldReturnInternalServerError_WhenServiceThrows() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new RuntimeException("Failed"))
                .when(productService).updateImageURL("admin", productId, "http://newimage.com");
        mockMvc.perform(put("/products/" + productId + "/image-url")
                .param("username", "admin")
                .param("imageUrl", "http://newimage.com"))
                .andExpect(status().isInternalServerError());
    }

}
