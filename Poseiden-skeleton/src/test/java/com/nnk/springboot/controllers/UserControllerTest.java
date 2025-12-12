package com.nnk.springboot.controllers;

import com.nnk.springboot.dto.UserDTO;
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
        UserDTO u1 = new UserDTO(1, "user1", "User One", "ADMIN");
        UserDTO u2 = new UserDTO(2, "user2", "User Two", "USER");
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
        UserDTO u = new UserDTO(1, "user1", "User One", "ADMIN");
        Mockito.when(userService.createUser(Mockito.any(UserDTO.class), Mockito.anyString())).thenReturn(u);
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
        UserDTO u = new UserDTO(1, "user1", "User One", "ADMIN");
        Mockito.when(userService.findById(1)).thenReturn(u);

        mockMvc.perform(get("/user/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(model().attributeExists("userDTO"));
    }

    @Test
    void updateUser_shouldRedirectOnSuccess() throws Exception {
        UserDTO u = new UserDTO(1, "user1", "User One", "ADMIN");
        Mockito.when(userService.updateUser(Mockito.eq(1), Mockito.any(UserDTO.class), Mockito.anyString())).thenReturn(u);
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
