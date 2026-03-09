package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.demo.facades.AdminFacade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;

class AdminServiceTest {

        private AdminService adminService;

        @Mock
        private AdminFacade adminFacade;

        private AutoCloseable mocks;

        private static final String USER = "omer";
        private static final String PASS = "pass123";

        @BeforeEach
        void setUp() {
                mocks = MockitoAnnotations.openMocks(this);
                adminService = AdminService.getInstance(adminFacade);
        }

        @AfterEach
        void tearDown() throws Exception {
                AdminService.resetInstance();
                mocks.close();
        }

        // ---------- Add Admin ----------
        @Test
        void testAddAdminSuccess() {
                adminService.addAdmin(USER, PASS);
                verify(adminFacade).addAdmin(USER, PASS);
                when(adminFacade.login(USER, PASS)).thenReturn(true);
                assertTrue(adminService.login(USER, PASS));
                verify(adminFacade).login(USER, PASS);
        }

        @Test
        void testAddAdminDuplicate() {
                doNothing().when(adminFacade).addAdmin(USER, PASS);
                adminService.addAdmin(USER, PASS);
                verify(adminFacade).addAdmin(USER, PASS);
                doThrow(new IllegalArgumentException("Admin already exists"))
                                .when(adminFacade).addAdmin(USER, "pass456");
                Exception ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.addAdmin(USER, "pass456"));
                assertEquals("Admin already exists", ex.getMessage());
                verify(adminFacade).addAdmin(USER, "pass456");
        }

        @Test
        void testAddAdminInvalidInput() {
                doThrow(new IllegalArgumentException("Username and password cannot be empty"))
                                .when(adminFacade).addAdmin("", PASS);
                Exception ex1 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.addAdmin("", PASS));
                assertEquals("Username and password cannot be empty", ex1.getMessage());
                doThrow(new IllegalArgumentException("Username and password cannot be empty"))
                                .when(adminFacade).addAdmin(USER, "");
                Exception ex2 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.addAdmin(USER, ""));
                assertEquals("Username and password cannot be empty", ex2.getMessage());
                verify(adminFacade).addAdmin("", PASS);
                verify(adminFacade).addAdmin(USER, "");
        }

        @Test
        void testAddAdminNullInput() {
                doThrow(new IllegalArgumentException("Username and password cannot be null"))
                                .when(adminFacade).addAdmin(eq(null), eq(PASS));
                Exception ex1 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.addAdmin(null, PASS));
                assertEquals("Username and password cannot be null", ex1.getMessage());
                doThrow(new IllegalArgumentException("Username and password cannot be null"))
                                .when(adminFacade).addAdmin(eq(USER), eq(null));
                Exception ex2 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.addAdmin(USER, null));
                assertEquals("Username and password cannot be null", ex2.getMessage());
                verify(adminFacade).addAdmin(eq(null), eq(PASS));
                verify(adminFacade).addAdmin(eq(USER), eq(null));
        }

        // ---------- Login ----------
        @Test
        void testLoginSuccess() {
                when(adminFacade.login(USER, PASS)).thenReturn(true);
                assertTrue(adminService.login(USER, PASS));
                verify(adminFacade).login(USER, PASS);
        }

        @Test
        void testLoginWrongPassword() {
                when(adminFacade.login(USER, "wrongPass")).thenReturn(false);
                assertFalse(adminService.login(USER, "wrongPass"));
                verify(adminFacade).login(USER, "wrongPass");
        }

        @Test
        void testLoginUnknownUser() {
                when(adminFacade.login("nonexistent", PASS)).thenReturn(false);
                assertFalse(adminService.login("nonexistent", PASS));
                verify(adminFacade).login("nonexistent", PASS);
        }

        @Test
        void testLoginInvalidInput() {
                doThrow(new IllegalArgumentException("Username and password cannot be empty"))
                                .when(adminFacade).login("", PASS);
                Exception ex1 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.login("", PASS));
                assertEquals("Username and password cannot be empty", ex1.getMessage());
                doThrow(new IllegalArgumentException("Username and password cannot be empty"))
                                .when(adminFacade).login(USER, "");
                Exception ex2 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.login(USER, ""));
                assertEquals("Username and password cannot be empty", ex2.getMessage());
                verify(adminFacade).login("", PASS);
                verify(adminFacade).login(USER, "");
        }

        @Test
        void testLoginNullInput() {
                doThrow(new IllegalArgumentException("Username and password cannot be null"))
                                .when(adminFacade).login(eq(null), eq(PASS));
                Exception ex1 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.login(null, PASS));
                assertEquals("Username and password cannot be null", ex1.getMessage());
                doThrow(new IllegalArgumentException("Username and password cannot be null"))
                                .when(adminFacade).login(eq(USER), eq(null));
                Exception ex2 = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.login(USER, null));
                assertEquals("Username and password cannot be null", ex2.getMessage());
                verify(adminFacade).login(eq(null), eq(PASS));
                verify(adminFacade).login(eq(USER), eq(null));
        }

        // ---------- Logout ----------
        @Test
        void testLogoutSuccess() {
                when(adminFacade.logout("admin")).thenReturn(true);
                boolean result = adminService.logout("admin");
                assertTrue(result);
                verify(adminFacade).logout("admin");
        }

        @Test
        void testLogoutWithoutLogin() {
                when(adminFacade.logout("admin")).thenReturn(false);
                boolean result = adminService.logout("admin");
                assertFalse(result); 
                verify(adminFacade).logout("admin");
        }

        @Test
        void testLogoutUnknownUser() {
                when(adminFacade.logout("unknown")).thenReturn(false);
                boolean result = adminService.logout("unknown");
                assertFalse(result); 
                verify(adminFacade).logout("unknown");
        }

        @Test
        void testLogoutNullUsername() {
                doThrow(new IllegalArgumentException("Username cannot be null or empty"))
                                .when(adminFacade).logout(eq(null));
                Exception ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> adminService.logout(null));
                assertEquals("Username cannot be null or empty", ex.getMessage());
                verify(adminFacade).logout(eq(null));
        }

}