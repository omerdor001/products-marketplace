package com.example.demo.repositories;

import org.springframework.stereotype.Repository;

import com.example.demo.data_access.JpaAdminRepository;
import com.example.demo.domain.Admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class Admins_DB_Repository implements AdminRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final JpaAdminRepository dbRepository;

    public Admins_DB_Repository(JpaAdminRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void addAdmin(String username, String password) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty");
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be null or empty");
        if (dbRepository.existsByUsername(username))
            throw new IllegalArgumentException("Admin already exists");
        Admin admin = new Admin(username, password);
        entityManager.persist(admin);
    }

    @Override
    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty");
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be null or empty");
        Admin adminOpt = dbRepository.findByUsername(username);
        if (adminOpt != null && adminOpt.getEncryptedPassword().equals(password)) {
            adminOpt.setLogged(true);
            entityManager.merge(adminOpt);
            return true; 
        }
        return false;
    }

    @Override
    public boolean logout(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty");
        Admin adminOpt = dbRepository.findByUsername(username);
        if (adminOpt != null && adminOpt.isLogged()) {
            adminOpt.setLogged(false);
            entityManager.merge(adminOpt);
            return true;
        }
        return false;
    }

    @Override
    public boolean isAdminLoggedIn(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty");
        return dbRepository.existsByUsername(username);
    }
}
