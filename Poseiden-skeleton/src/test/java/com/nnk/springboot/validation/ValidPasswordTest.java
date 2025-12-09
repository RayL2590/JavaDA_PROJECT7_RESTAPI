package com.nnk.springboot.validation;

import com.nnk.springboot.domain.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidPasswordTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenPasswordIsValid_thenValidationSucceeds() {
        User user = new User();
        user.setUsername("testUser");
        user.setFullname("Test User");
        user.setRole("USER");
        // Mot de passe valide : Majuscule, chiffre, symbole, 8+ caractères
        user.setPassword("Password123!"); 

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Le mot de passe devrait être valide");
    }

    @Test
    void whenPasswordIsTooSimple_thenValidationFails() {
        User user = new User();
        user.setUsername("testUser");
        user.setFullname("Test User");
        user.setRole("USER");
        // Mot de passe invalide (pas de symbole, pas de majuscule)
        user.setPassword("weakpassword"); 

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("Password must be at least 8 characters")), 
            "Doit contenir le message d'erreur de @ValidPassword");
    }
}