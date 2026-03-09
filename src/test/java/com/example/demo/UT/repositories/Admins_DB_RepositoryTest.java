package com.example.demo.UT.repositories;

import com.example.demo.data_access.JpaAdminRepository;
import com.example.demo.domain.Admin;
import com.example.demo.repositories.Admins_DB_Repository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class Admins_DB_RepositoryTest {

    private JpaAdminRepository jpaRepository;
    private EntityManager entityManager;
    private Admins_DB_Repository repository;

    @BeforeEach
    void setup() {
        jpaRepository = mock(JpaAdminRepository.class);
        entityManager = mock(EntityManager.class);
        repository = new Admins_DB_Repository(jpaRepository);
        repository.setEntityManager(entityManager);
    }

    // ---------- Add Admin ----------

    @Test
    void addAdmin_success() {
        when(jpaRepository.existsByUsername("admin")).thenReturn(false);
        repository.addAdmin("admin", "password");
        verify(entityManager).persist(any(Admin.class));
    }

    @Test
    void addAdmin_existingAdmin_shouldThrow() {
        when(jpaRepository.existsByUsername("admin")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> repository.addAdmin("admin", "password"));
    }

    // ---------- Login ----------

    @Test
    void login_success() {
        Admin admin = new Admin("admin", "password");
        when(jpaRepository.findByUsername("admin")).thenReturn(admin);
        boolean result = repository.login("admin", "password");
        assertTrue(result);
    }

    @Test
    void login_wrongPassword() {
        Admin admin = new Admin("admin", "password");
        when(jpaRepository.findByUsername("admin")).thenReturn(admin);
        boolean result = repository.login("admin", "wrong");
        assertFalse(result);
    }

    @Test
    void login_nullPassword() {
        Admin admin = new Admin("admin", "password");
        when(jpaRepository.findByUsername("admin")).thenReturn(admin);
        assertThrows(IllegalArgumentException.class, () -> {
            repository.login("admin", null);
        });
    }

    @Test
    void login_emptyPassword() {
        Admin admin = new Admin("admin", "password");
        when(jpaRepository.findByUsername("admin")).thenReturn(admin);
        assertThrows(IllegalArgumentException.class, () -> {
            repository.login("admin", "");
        });
    }

    @Test
    void login_nullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.login(null, "password");
        });
    }

    @Test
    void login_emptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.login("", "password");
        });
    }

    @Test
    void login_adminNotFound() {
        when(jpaRepository.findByUsername("admin")).thenReturn(null);
        boolean result = repository.login("admin", "password");
        assertFalse(result);
    }

    // ---------- Logout ---------

    @Test
    void logout_success() {
        Admin admin = new Admin("admin", "password");
        when(jpaRepository.findByUsername("admin")).thenReturn(admin);
        repository.login("admin", "password");
        boolean result = repository.logout("admin");
        assertTrue(result);
    }

    @Test
    void logout_notLoggedIn() {
        Admin admin = new Admin("admin", "password");
        when(jpaRepository.findByUsername("admin")).thenReturn(admin);
        boolean result = repository.logout("admin");
        assertFalse(result);
    }

    @Test
    void logout_nullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.logout(null);
        });
    }


}