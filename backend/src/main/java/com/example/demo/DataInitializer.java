package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.service.AdminService;
import com.example.demo.service.ProductService;
import com.example.demo.domain.Coupon;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(AdminService adminService, ProductService productService) {
        return args -> {
            adminService.addAdmin("admin1", "password123");
            adminService.login("admin1", "password123");
            productService.addCoupon(
                    "admin1",
                    "Amazon Gift Card",
                    "Amazon $100 Gift Card",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQI3SckxRKUPTCIai1-ONmFz6kWAI94VA8TVw&s",
                    80.0,
                    20.0,
                    Coupon.ValueType.STRING,
                    "100"
            );
            productService.addCoupon(
                    "admin1",
                    "Steam Wallet",
                    "Steam Wallet Code",
                    "https://backend.odigix.com/storage/141/conversions/steammain-large.jpg",
                    40.0,
                    25.0,
                    Coupon.ValueType.STRING,
                    "50"
            );
            adminService.logout("admin1");
            System.out.println("Initial data loaded successfully.");
        };
    }
}