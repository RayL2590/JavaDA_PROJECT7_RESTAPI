package com.nnk.springboot.validation;

import com.nnk.springboot.domain.Rating;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtLeastOneRatingPresentValidator - Tests unitaires")
class AtLeastOneRatingPresentValidatorTest {

    private AtLeastOneRatingPresentValidator validator;

    @Mock
    private ConstraintValidatorContext context;
    @BeforeEach
    void setUp() {
        validator = new AtLeastOneRatingPresentValidator();
    }

    @Test
    @DisplayName("isValid() - Avec rating null - Doit retourner true (géré par @NotNull)")
    void isValid_WithNullRating_ShouldReturnTrue() {
        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid() - Avec au moins Moody's - Doit retourner true")
    void isValid_WithOnlyMoodys_ShouldReturnTrue() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating("Aaa");
        rating.setSandPRating(null);
        rating.setFitchRating(null);

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid() - Avec au moins S&P - Doit retourner true")
    void isValid_WithOnlySP_ShouldReturnTrue() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating(null);
        rating.setSandPRating("AAA");
        rating.setFitchRating(null);

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid() - Avec au moins Fitch - Doit retourner true")
    void isValid_WithOnlyFitch_ShouldReturnTrue() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating(null);
        rating.setSandPRating(null);
        rating.setFitchRating("AAA");

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid() - Avec toutes les notations - Doit retourner true")
    void isValid_WithAllRatings_ShouldReturnTrue() {
        // Given
        Rating rating = new Rating("Aaa", "AAA", "AAA");

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValid() - Avec toutes notations null - Doit retourner false")
    void isValid_WithAllNullRatings_ShouldReturnFalse() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating(null);
        rating.setSandPRating(null);
        rating.setFitchRating(null);

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid() - Avec toutes notations vides - Doit retourner false")
    void isValid_WithAllEmptyRatings_ShouldReturnFalse() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating("");
        rating.setSandPRating("   ");
        rating.setFitchRating("");

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid() - Avec mélange null et vide - Doit retourner false")
    void isValid_WithMixedNullAndEmpty_ShouldReturnFalse() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating(null);
        rating.setSandPRating("");
        rating.setFitchRating("   ");

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValid() - Avec deux notations vides et une valide - Doit retourner true")
    void isValid_WithTwoEmptyAndOneValid_ShouldReturnTrue() {
        // Given
        Rating rating = new Rating();
        rating.setMoodysRating("");
        rating.setSandPRating("AAA");
        rating.setFitchRating(null);

        // When
        boolean result = validator.isValid(rating, context);

        // Then
        assertThat(result).isTrue();
    }
}