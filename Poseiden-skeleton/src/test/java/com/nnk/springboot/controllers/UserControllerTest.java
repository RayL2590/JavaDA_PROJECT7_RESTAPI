package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
@WithMockUser(roles = "ADMIN")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void home_shouldDisplayUserList() throws Exception {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("user1");
        u1.setPassword("Password1!");
        u1.setFullname("User One");
        u1.setRole("ADMIN");
        User u2 = new User();
        u2.setId(2);
        u2.setUsername("user2");
        u2.setPassword("Password2!");
        u2.setFullname("User Two");
        u2.setRole("USER");
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void addUser_shouldDisplayAddForm() throws Exception {
        mockMvc.perform(get("/user/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"));
    }

    @Test
    void validate_shouldRedirectOnSuccess() throws Exception {
        User u = new User();
        u.setId(1);
        u.setUsername("user1");
        u.setPassword("Password1!");
        u.setFullname("User One");
        u.setRole("ADMIN");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(u);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(u));

        mockMvc.perform(post("/user/validate")
                .param("username", "user1")
                .param("password", "Password1!")
                .param("fullname", "User One")
                .param("role", "ADMIN")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }

    @Test
    void validate_shouldReturnFormOnValidationError() throws Exception {
        mockMvc.perform(post("/user/validate")
                .param("username", "") // empty username
                .param("password", "Password1!")
                .param("fullname", "User One")
                .param("role", "ADMIN")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"));
    }

    @Test
    void showUpdateForm_shouldDisplayUpdateForm() throws Exception {
        User u = new User();
        u.setId(1);
        u.setUsername("user1");
        u.setPassword("");
        u.setFullname("User One");
        u.setRole("ADMIN");
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(u));

        mockMvc.perform(get("/user/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void updateUser_shouldRedirectOnSuccess() throws Exception {
        User u = new User();
        u.setId(1);
        u.setUsername("user1");
        u.setPassword("Password1!");
        u.setFullname("User One");
        u.setRole("ADMIN");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(u);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(u));

        mockMvc.perform(post("/user/update/1")
                .param("username", "user1")
                .param("password", "Password1!")
                .param("fullname", "User One")
                .param("role", "ADMIN")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }

    @Test
    void updateUser_shouldReturnFormOnValidationError() throws Exception {
        mockMvc.perform(post("/user/update/1")
                .param("username", "") // empty username
                .param("password", "Password1!")
                .param("fullname", "User One")
                .param("role", "ADMIN")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"));
    }

    @Test
    void deleteUser_shouldRedirectOnSuccess() throws Exception {
        User u = new User();
        u.setId(1);
        u.setUsername("user1");
        u.setPassword("Password1!");
        u.setFullname("User One");
        u.setRole("ADMIN");
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(u));
        Mockito.doNothing().when(userRepository).delete(u);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/user/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }
}
