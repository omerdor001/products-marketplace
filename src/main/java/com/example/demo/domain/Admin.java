package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String encryptedPassword;
    private boolean isLogged;

    public Admin(String username, String encryptedPassword) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.isLogged = false;
    }
    public Admin() {}

    // ---------- Getters ----------

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public boolean isLogged() {
        return isLogged;
    }

    // ---------- Setters ----------

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
