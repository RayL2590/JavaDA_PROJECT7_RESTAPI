package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        return toDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO, String rawPassword) {
        User user = toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(rawPassword));
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    public UserDTO updateUser(Integer id, UserDTO userDTO, String rawPassword) {
        User user = toEntity(userDTO);
        user.setId(id);
        user.setPassword(passwordEncoder.encode(rawPassword));
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getFullname(), user.getRole());
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setFullname(dto.getFullname());
        user.setRole(dto.getRole());
        return user;
    }
}