package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class Admins_Memory_RepositoryTest {
    private Admins_Memory_Repository repo = Admins_Memory_Repository.getInstance();
    
    @BeforeEach
    void setUp() {
        repo = Admins_Memory_Repository.getInstance();
    }

    @AfterEach
    void tearDown() {
        Admins_Memory_Repository.resetInstance();
    }

    // ---------- Add Admin ----------
    @Test
    void testAddAdminNullUsername() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                repo.addAdmin(null, "password123")
        );
        assertEquals("Username cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddAdminEmptyUsername() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                repo.addAdmin("   ", "password123")
        );
        assertEquals("Username cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddAdminNullPassword() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                repo.addAdmin("admin", null)
        );
        assertEquals("Password cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddAdminEmptyPassword() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                repo.addAdmin("admin", "   ")
        );
        assertEquals("Password cannot be null or empty", ex.getMessage());
    }

    @Test
    void testAddAdminDuplicateUsername() {
        repo.addAdmin("admin1", "password123");
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                repo.addAdmin("admin1", "anotherPass")
        );
        assertEquals("Admin already exists", ex.getMessage());
    }

    // ---------- Login ----------
    @Test
    void testLoginSuccess() {
        repo.addAdmin("admin1", "password1");
        assert repo.login("admin1", "password1");
    }

    @Test
    void testLoginFailure() {
        repo.addAdmin("admin2", "password2");
        assert !repo.login("admin2", "wrongpassword");
    }

    @Test
    void testLoginNonExistentAdmin() {
        assert !repo.login("nonexistent", "password");      
    }

  
}
