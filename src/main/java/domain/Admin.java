package domain;

public class Admin {
    private String username;
    private String encryptedPassword;
    private boolean isLogged;

    public Admin(String username, String encryptedPassword) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.isLogged = false;
    }

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
