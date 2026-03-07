package business;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Admins_Memory_RepositoryTest {
    private Admins_Memory_Repository repo = new Admins_Memory_Repository();
    
    @BeforeEach
    void setUp() {
        repo = new Admins_Memory_Repository();
    }

    @AfterEach
    void tearDown() {
        repo = null; 
    }

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
