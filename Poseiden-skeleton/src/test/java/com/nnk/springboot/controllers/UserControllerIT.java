package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void home_shouldReturnUserList() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setFullname("Test User");
        user.setRole("USER");
        user.setPassword("Password123!");
        userRepository.save(user);

        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void validate_shouldCreateUser() throws Exception {
        mockMvc.perform(post("/user/validate")
                .param("username", "newuser")
                .param("fullname", "New User")
                .param("role", "USER")
                .param("password", "Password123!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        assertTrue(userRepository.findByUsername("newuser").isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUser_shouldUpdateUser() throws Exception {
        User user = new User();
        user.setUsername("olduser");
        user.setFullname("Old User");
        user.setRole("USER");
        user.setPassword("Password123!");
        user = userRepository.save(user);

        mockMvc.perform(post("/user/update/" + user.getId())
                .param("username", "updateduser")
                .param("fullname", "Updated User")
                .param("role", "ADMIN")
                .param("password", "NewPassword123!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("Updated User", updatedUser.getFullname());
        assertEquals("ADMIN", updatedUser.getRole());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_shouldDeleteUser() throws Exception {
        User user = new User();
        user.setUsername("todelete");
        user.setFullname("To Delete");
        user.setRole("USER");
        user.setPassword("Password123!");
        user = userRepository.save(user);

        mockMvc.perform(post("/user/delete/" + user.getId())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}
