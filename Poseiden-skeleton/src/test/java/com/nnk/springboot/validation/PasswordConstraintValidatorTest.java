package com.nnk.springboot.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConstraintValidatorTest {

    private PasswordConstraintValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordConstraintValidator();
    }

    @Test
    void validPassword_shouldReturnTrue() {
        assertTrue(validator.isValid("Password1!", null));
        assertTrue(validator.isValid("Abcdef1@", null));
        assertTrue(validator.isValid("A1!aaaaa", null));
    }

    @Test
    void passwordTooShort_shouldReturnFalse() {
        assertFalse(validator.isValid("A1!", null));
        assertFalse(validator.isValid("A1!a", null));
    }

    @Test
    void passwordMissingUppercase_shouldReturnFalse() {
        assertFalse(validator.isValid("password1!", null));
    }

    @Test
    void passwordMissingDigit_shouldReturnFalse() {
        assertFalse(validator.isValid("Password!", null));
    }

    @Test
    void passwordMissingSymbol_shouldReturnFalse() {
        assertFalse(validator.isValid("Password1", null));
    }

    @Test
    void passwordIsNull_shouldReturnFalse() {
        assertFalse(validator.isValid(null, null));
    }
}
