package com.example.demo.service;

import com.example.demo.repositories.Admins_Memory_Repository;

public class AdminService {
    private static AdminService instance;
    private Admins_Memory_Repository adminsRepository;

    private AdminService() {
        adminsRepository = Admins_Memory_Repository.getInstance();
    }

    public static AdminService getInstance() {
        if (instance == null) {
            instance = new AdminService();
        }
        return instance;
    }

    //For testing purposes only
    public static void resetInstance() {
        Admins_Memory_Repository.resetInstance();
        instance = null;
    }

    public boolean login(String username, String password) {
        return adminsRepository.login(username, password);
    } 
    
    public void addAdmin(String username, String password) {
        adminsRepository.addAdmin(username, password);
    }
}
