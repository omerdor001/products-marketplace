package com.example.demo.IT;

import com.example.demo.controllers.CustomerController;
import com.example.demo.domain.Coupon;
import com.example.demo.domain.Coupon.ValueType;
import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class Customer_Controller_Tests {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ProductService productService;

        @MockBean
        private PurchaseService purchaseService;

        @Test
        void getAvailableProducts_ShouldReturnProducts() throws Exception {
                List<Product> mockProducts = Arrays.asList(
                                new Coupon("Name", "Desc", "http://img.com", 10.0, 20.0, ValueType.STRING, "100"),
                                new Coupon("Name 2", "Desc 2", "http://img2.com", 15.0, 25.0, ValueType.STRING, "101"));
                when(productService.getAvailableProducts()).thenReturn(mockProducts);
                mockMvc.perform(get("/customer/products")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].name").value("Name"))
                                .andExpect(jsonPath("$[1].price").value(18.75));
                verify(productService).getAvailableProducts();
        }

        @Test
        void getAvailableProducts_ShouldReturnInternalServerError_WhenException() throws Exception {
                when(productService.getAvailableProducts()).thenThrow(new RuntimeException("DB failure"));
                mockMvc.perform(get("/customer/products")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string(""));
        }

        @Test
        void purchaseProductByCustomer_ShouldReturnConfirmation() throws Exception {
                UUID productId = UUID.randomUUID();
                String confirmationMessage = "Product purchased successfully!";
                when(purchaseService.purchaseProductByCustomer(productId)).thenReturn(confirmationMessage);
                mockMvc.perform(get("/customer/purchase/" + productId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string(confirmationMessage));
                verify(purchaseService).purchaseProductByCustomer(productId);
        }

        @Test
        void purchaseProductByCustomer_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
                UUID productId = UUID.randomUUID();
                doThrow(new RuntimeException("Purchase failed"))
                                .when(purchaseService)
                                .purchaseProductByCustomer(productId);
                mockMvc.perform(get("/customer/purchase/" + productId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError());
        }
}
