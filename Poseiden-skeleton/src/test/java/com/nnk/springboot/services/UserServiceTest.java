package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
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
    void findAll_shouldReturnUserList() {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("user1");
        User u2 = new User();
        u2.setId(2);
        u2.setUsername("user2");
        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        assertEquals(2, userService.findAll().size());
    }

    @Test
    void findById_shouldReturnUser() {
        User u = new User();
        u.setId(1);
        u.setUsername("user1");
        when(userRepository.findById(1)).thenReturn(Optional.of(u));

        User result = userService.findById(1);
        assertEquals("user1", result.getUsername());
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.findById(1));
    }

    @Test
    void createUser_shouldEncodePasswordAndSave() {
        User u = new User();
        u.setPassword("Password1!");
        when(encoder.encode("Password1!")).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(u);
        assertEquals("encodedPwd", result.getPassword());
        verify(userRepository).save(u);
    }

    @Test
    void updateUser_shouldEncodePasswordAndSave() {
        User u = new User();
        u.setPassword("Password1!");
        when(encoder.encode("Password1!")).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser(5, u);
        assertEquals(5, result.getId());
        assertEquals("encodedPwd", result.getPassword());
        verify(userRepository).save(u);
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
