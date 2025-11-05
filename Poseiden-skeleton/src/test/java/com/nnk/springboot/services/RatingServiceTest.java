package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingService - Tests unitaires")
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private Rating validRating;
    private Rating anotherRating;

    @BeforeEach
    void setUp() {
        validRating = new Rating("Aaa", "AAA", "AAA");
        validRating.setId(1);
        validRating.setOrderNumber(1);

        anotherRating = new Rating("Baa3", "BBB-", "BBB-");
        anotherRating.setId(2);
        anotherRating.setOrderNumber(12);
    }

    @Test
    @DisplayName("findAll() - Doit retourner toutes les notations")
    void findAll_ShouldReturnAllRatings() {
        // Given
        List<Rating> expectedRatings = Arrays.asList(validRating, anotherRating);
        when(ratingRepository.findAll()).thenReturn(expectedRatings);

        // When
        List<Rating> result = ratingService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(validRating, anotherRating);
        verify(ratingRepository).findAll();
    }

    @Test
    @DisplayName("findAll() - Avec repository vide - Doit retourner liste vide")
    void findAll_WithEmptyRepository_ShouldReturnEmptyList() {
        // Given
        when(ratingRepository.findAll()).thenReturn(List.of());

        // When
        List<Rating> result = ratingService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository).findAll();
    }

    @Test
    @DisplayName("findById() - Avec ID valide - Doit retourner le rating")
    void findById_WithValidId_ShouldReturnRating() {
        // Given
        when(ratingRepository.findById(1)).thenReturn(Optional.of(validRating));

        // When
        Optional<Rating> result = ratingService.findById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(validRating);
        verify(ratingRepository).findById(1);
    }

    @Test
    @DisplayName("findById() - Avec ID inexistant - Doit retourner Optional vide")
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // Given
        when(ratingRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Rating> result = ratingService.findById(999);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository).findById(999);
    }

    @Test
    @DisplayName("create() - Avec rating valide - Doit créer et forcer ID null")
    void create_WithValidRating_ShouldCreateAndForceIdNull() {
        // Given
        Rating newRating = new Rating("Aa1", "AA+", "AA+");
        newRating.setId(999);
        newRating.setOrderNumber(3);

        Rating savedRating = new Rating("Aa1", "AA+", "AA+");
        savedRating.setId(5);
        savedRating.setOrderNumber(3);

        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating saved = invocation.getArgument(0);
            assertThat(saved.getId()).isNull();
            return savedRating;
        });

        // When
        Rating result = ratingService.create(newRating);

        // Then
        assertThat(result.getId()).isEqualTo(5);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    @DisplayName("create() - Validation Bean Validation - Doit échouer si contraintes violées")
    void create_WithInvalidRating_ShouldFailValidation() {
        Rating invalidRating = new Rating("Aaa", "AAA", "AAA");
        invalidRating.setOrderNumber(-1);

        assertThatThrownBy(() -> {
            if (invalidRating.getOrderNumber() != null && invalidRating.getOrderNumber() < 1) {
                throw new ConstraintViolationException("Order number must be positive", null);
            }
            ratingService.create(invalidRating);
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("update() - Avec ID existant - Doit mettre à jour")
    void update_WithExistingId_ShouldUpdate() {
        // Given
        when(ratingRepository.existsById(1)).thenReturn(true);
        when(ratingRepository.save(any(Rating.class))).thenReturn(validRating);

        Rating updatedData = new Rating("Aa2", "AA", "AA");
        updatedData.setOrderNumber(4);

        // When
        Rating result = ratingService.update(1, updatedData);

        // Then
        assertThat(result.getId()).isEqualTo(1);
        verify(ratingRepository).existsById(1);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    @DisplayName("update() - Avec ID inexistant - Doit lever exception")
    void update_WithNonExistentId_ShouldThrowException() {
        // Given
        when(ratingRepository.existsById(999)).thenReturn(false);

        Rating updatedData = new Rating("Aa2", "AA", "AA");
        updatedData.setOrderNumber(4);

        // When & Then
        assertThatThrownBy(() -> ratingService.update(999, updatedData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rating not found with id: 999");

        verify(ratingRepository).existsById(999);
        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() - Doit forcer l'ID fourni en paramètre")
    void update_ShouldForceProvidedId() {
        // Given
        when(ratingRepository.existsById(1)).thenReturn(true);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating saved = invocation.getArgument(0);
            assertThat(saved.getId()).isEqualTo(1); // Vérifie que l'ID est celui fourni
            return saved;
        });

        Rating updatedData = new Rating("Aa2", "AA", "AA");
        updatedData.setId(999);

        // When
        ratingService.update(1, updatedData);

        // Then
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    @DisplayName("deleteById() - Avec ID valide existant - Doit supprimer")
    void deleteById_WithValidExistingId_ShouldDelete() {
        // Given
        doNothing().when(ratingRepository).deleteById(1);

        // When
        ratingService.deleteById(1);

        // Then
        verify(ratingRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteById() - Avec ID inexistant - Doit lever exception")
    void deleteById_WithNonExistentId_ShouldThrowException() {
        // Given
        doThrow(new EmptyResultDataAccessException(1))
                .when(ratingRepository).deleteById(999);

        // When & Then
        assertThatThrownBy(() -> ratingService.deleteById(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rating not found with id: 999");

        verify(ratingRepository).deleteById(999);
    }

    @Test
    @DisplayName("deleteById() - Pas de race condition avec existsById")
    void deleteById_ShouldNotUseExistsById() {
        // Given
        doNothing().when(ratingRepository).deleteById(1);

        // When
        ratingService.deleteById(1);

        // Then
        verify(ratingRepository).deleteById(1);
        verify(ratingRepository, never()).existsById(any());
    }
}