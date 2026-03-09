package com.example.demo.IT;

import com.example.demo.controllers.AdminController;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class Admin_Controller_Tests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void login_ShouldReturnOk() throws Exception {
        String jsonContent = "{\"username\":\"admin\",\"password\":\"1234\"}";
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk());
        verify(adminService).login("admin", "1234");
    }

    @Test
    void logout_ShouldReturnOk() throws Exception {
        String jsonContent = "{\"username\":\"admin\"}";
        mockMvc.perform(post("/admin/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk());
        verify(adminService).logout("admin");
    }

    @Test
    void login_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        String jsonContent = "{\"username\":\"admin\",\"password\":\"1234\"}";
        doThrow(new RuntimeException("Login failed"))
                .when(adminService)
                .login("admin", "1234");
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void logout_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        String jsonContent = "{\"username\":\"admin\"}";
        doThrow(new RuntimeException("Logout failed"))
                .when(adminService)
                .logout("admin");
        mockMvc.perform(post("/admin/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isInternalServerError());
    }
}