package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
@WithMockUser(roles = "ADMIN")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void home_shouldDisplayUserList() throws Exception {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("user1");
        User u2 = new User();
        u2.setId(2);
        u2.setUsername("user2");
        Mockito.when(userService.findAll()).thenReturn(Arrays.asList(u1, u2));

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
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(u);
        Mockito.when(userService.findAll()).thenReturn(Arrays.asList(u));

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
        Mockito.when(userService.findById(1)).thenReturn(u);

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
        Mockito.when(userService.updateUser(Mockito.eq(1), Mockito.any(User.class))).thenReturn(u);
        Mockito.when(userService.findAll()).thenReturn(Arrays.asList(u));

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
        Mockito.doNothing().when(userService).deleteUser(1);
        Mockito.when(userService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(post("/user/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));
    }
}
