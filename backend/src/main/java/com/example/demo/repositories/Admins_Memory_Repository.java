package com.example.demo.repositories;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.domain.Admin;

public class Admins_Memory_Repository implements AdminRepository {
    private List<Admin> admins;

    private static Admins_Memory_Repository instance;

    private Admins_Memory_Repository() {
        this.admins = new ArrayList<>();
    }

    public static synchronized Admins_Memory_Repository getInstance() {
        if (instance == null) {
            instance = new Admins_Memory_Repository();
        }
        return instance;
    }

    // For testing purposes only
    public static void resetInstance() {
        instance = null;
    }

    public void addAdmin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        boolean exists = admins.stream()
                .anyMatch(a -> a.getUsername().equalsIgnoreCase(username));
        if (exists) {
            throw new IllegalArgumentException("Admin already exists");
        }
        Admin admin = new Admin(username, password);
        admins.add(admin);
    }

    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        System.out.println(admins.size());
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username) && admin.getEncryptedPassword().equals(password)) {
                admin.setLogged(true);
                return true; 
            }
        }
        return false;
    }

    public boolean logout(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        for (Admin admin : admins) {
            if(isAdminLoggedIn(username)) {
                admin.setLogged(false);
                return true;
            }
        }
        return false;
    }

    public boolean isAdminLoggedIn(String username) {
    if (username == null || username.trim().isEmpty()) {
        throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return admins.stream()
            .filter(admin -> admin.getUsername().equals(username))
            .findFirst() 
            .map(admin -> {
                return admin.isLogged(); 
            })
            .orElseGet(() -> {
                return false;
            });
}

}
