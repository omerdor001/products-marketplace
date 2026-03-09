package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.facades.AdminFacade;

@Service
public class AdminService {

    private static AdminService instance;
    private AdminFacade adminFacade;

    private AdminService(AdminFacade adminsFacade) {
        this.adminFacade = adminsFacade;
    }

    public static AdminService getInstance(AdminFacade facade) {
        if (instance == null) {
            instance = new AdminService(facade);
        }
        return instance;
    }

    // For testing purposes
    public static void resetInstance() {
        instance = null;
    }

    public boolean login(String username, String password) {
        return adminFacade.login(username, password);
    }

    public boolean logout(String username) {
        return adminFacade.logout(username);
    }

    public void addAdmin(String username, String password) {
        adminFacade.addAdmin(username, password);
    }
}