package com.example.demo.IT;

import com.example.demo.domain.Admin;
import com.example.demo.data_access.JpaAdminRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class Admins_DB_Tests {
    @Autowired
    private JpaAdminRepository jpaRepository;

     // ---------------- Add ----------------
     
    @Test
    void addAdmin_shouldPersistAdminInDatabase() {
        Admin admin = new Admin("admin", "password");
        jpaRepository.save(admin);
        Admin found = jpaRepository.findByUsername("admin");
        assertNotNull(found);
        assertEquals("admin", found.getUsername());
    }
}
