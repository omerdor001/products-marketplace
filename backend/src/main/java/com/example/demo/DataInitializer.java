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
                    "https://images.unsplash.com/photo-1523474253046-8cd2748b5fd2?w=400&h=300&fit=crop",
                    80.0,
                    20.0,
                    Coupon.ValueType.STRING,
                    "DEF-456"
            );
            productService.addCoupon(
                    "admin1",
                    "Steam Wallet",
                    "Steam Wallet Code",
                    "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop",
                    40.0,
                    25.0,
                    Coupon.ValueType.STRING,
                    "ABC-123"
            );
            adminService.logout("admin1");
            System.out.println("Initial data loaded successfully.");
        };
    }
}