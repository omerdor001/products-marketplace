package domain;

import java.util.ArrayList;
import java.util.List;

public class Admins_Memory_Repository {
    private List<Admin> admins;
    public Admins_Memory_Repository() {
        this.admins = new ArrayList<>();
    }

    public void addAdmin(String username, String password) {
        Admin admin = new Admin(username, password);
        admins.add(admin);
    }

    public boolean login(String username, String password) {
        return admins.stream()
                .anyMatch(admin -> admin.getUsername().equals(username) && admin.getEncryptedPassword().equals(password));
    }

}
