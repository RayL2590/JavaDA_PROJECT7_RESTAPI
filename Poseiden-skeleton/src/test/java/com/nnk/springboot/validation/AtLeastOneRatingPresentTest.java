package com.nnk.springboot.validation;

import com.nnk.springboot.domain.Rating;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtLeastOneRatingPresentTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllRatingsAreMissing_thenValidationFails() {
        // Given
        Rating rating = new Rating();

        // When
        Set<ConstraintViolation<Rating>> violations = validator.validate(rating);

        // Then
        assertFalse(violations.isEmpty(), "Devrait échouer car aucun rating n'est présent");
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().contains("At least one rating")), "Le message d'erreur doit correspondre");
    }

    @Test
    void whenMoodysRatingIsPresent_thenValidationSucceeds() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating("Aaa");

        // When
        Set<ConstraintViolation<Rating>> violations = validator.validate(rating);

        // Then
        assertTrue(violations.isEmpty(), "Devrait réussir car Moodys est présent");
    }
}