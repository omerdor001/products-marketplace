package com.example.demo.data_access;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Admin;

public interface JpaAdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByUsername(String username);
    Admin findByUsername(String username);
}
