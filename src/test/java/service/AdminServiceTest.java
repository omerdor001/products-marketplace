package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

class AdminServiceTest {

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = AdminService.getInstance();
    }

    @AfterEach
    void tearDown() {
        AdminService.resetInstance();
    }

    @Test
    void testAddAdminSuccess() {
        adminService.addAdmin("omer", "pass123");
        assertTrue(adminService.login("omer", "pass123"));
    }

    @Test
    void testAddAdminDuplicate() {
        adminService.addAdmin("omer", "pass123");
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                adminService.addAdmin("omer", "pass456")
        );
        assertEquals("Admin already exists", ex.getMessage());
    }
    
    @Test
    void testLoginSuccess() {
        adminService.addAdmin("omer", "pass123");
        assertTrue(adminService.login("omer", "pass123"));
    }


    @Test
    void testLoginWrongPassword() {
        adminService.addAdmin("omer", "pass123");
        assertFalse(adminService.login("omer", "wrongPass"));
    }

    @Test
    void testLoginUnknownUser() {
        assertFalse(adminService.login("nonexistent", "pass123"));
    }
}