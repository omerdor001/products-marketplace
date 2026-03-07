package domain;

import java.util.ArrayList;
import java.util.List;

public class Admins_Memory_Repository {
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

    //For testing purposes only
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
        return admins.stream()
                .anyMatch(
                        admin -> admin.getUsername().equals(username) && admin.getEncryptedPassword().equals(password));
    }

}
