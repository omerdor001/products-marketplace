package com.example.demo.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public void login(@RequestBody String username, String password) {
        adminService.login(username, password);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody String username) {
        adminService.logout(username);
    }
}
