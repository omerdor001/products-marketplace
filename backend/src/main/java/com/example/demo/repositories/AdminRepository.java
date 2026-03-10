package com.example.demo.repositories;

public interface AdminRepository {
    void addAdmin(String username, String password);
    boolean login(String username, String password);
    boolean logout(String username);
    boolean isAdminLoggedIn(String username);
}
