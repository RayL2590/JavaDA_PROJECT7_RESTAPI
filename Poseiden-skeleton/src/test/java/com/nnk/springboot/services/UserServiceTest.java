package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnUserDTOList() {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("user1");
        u1.setFullname("User One");
        u1.setRole("ADMIN");
        User u2 = new User();
        u2.setId(2);
        u2.setUsername("user2");
        u2.setFullname("User Two");
        u2.setRole("USER");
        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        assertEquals(2, userService.findAll().size());
        assertTrue(userService.findAll().get(0) instanceof UserDTO);
        assertEquals("user1", userService.findAll().get(0).getUsername());
    }

    @Test
    void findById_shouldReturnUserDTO() {
        User u = new User();
        u.setId(1);
        u.setUsername("user1");
        u.setFullname("User One");
        u.setRole("ADMIN");
        when(userRepository.findById(1)).thenReturn(Optional.of(u));

        UserDTO result = userService.findById(1);
        assertEquals("user1", result.getUsername());
        assertEquals("User One", result.getFullname());
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.findById(1));
    }

    @Test
    void createUser_shouldEncodePasswordAndSave() {
        UserDTO dto = new UserDTO(null, "user1", "User One", "ADMIN");
        String rawPassword = "Password1!";
        when(encoder.encode(rawPassword)).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDTO result = userService.createUser(dto, rawPassword);
        assertEquals("user1", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldEncodePasswordAndSave() {
        UserDTO dto = new UserDTO(null, "user1", "User One", "ADMIN");
        String rawPassword = "Password1!";
        when(encoder.encode(rawPassword)).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDTO result = userService.updateUser(5, dto, rawPassword);
        assertEquals("user1", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        User u = new User();
        u.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        doNothing().when(userRepository).delete(u);

        userService.deleteUser(1);
        verify(userRepository).delete(u);
    }
}
