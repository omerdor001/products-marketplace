package com.example.demo.facades;

import com.example.demo.repositories.Admins_DB_Repository;
import com.example.demo.repositories.Admins_Memory_Repository;

public class AdminFacade {
    private final Admins_DB_Repository dbRepository;
    private final Admins_Memory_Repository memoryRepository;

    public AdminFacade(Admins_DB_Repository dbRepository) {
        this.dbRepository = dbRepository;
        this.memoryRepository = Admins_Memory_Repository.getInstance();
    }

    public void addAdmin(String username, String password) {
        dbRepository.addAdmin(username, password);
        memoryRepository.addAdmin(username, password);
    }

    public boolean login(String username, String password) {
        boolean dbLogin = dbRepository.login(username, password);
        boolean memoryLogin = memoryRepository.login(username, password);
        return dbLogin && memoryLogin;
    }

}
