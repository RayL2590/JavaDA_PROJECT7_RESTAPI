package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurvePointService Tests")
class CurvePointServiceTest {

    @Mock
    private CurvePointRepository curvePointRepository;

    @InjectMocks
    private CurvePointService curvePointService;

    private CurvePoint validCurvePoint;

    @BeforeEach
    void setUp() {
    validCurvePoint = new CurvePoint();
    validCurvePoint.setCurveId(1);
    validCurvePoint.setTerm(new java.math.BigDecimal("1.0"));
    validCurvePoint.setValue(new java.math.BigDecimal("2.5"));
    validCurvePoint.setId(1);
    }

    @Test
    @DisplayName("Should create CurvePoint successfully")
    void shouldCreateCurvePointSuccessfully() {
        // Arrange
        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act
        CurvePoint created = curvePointService.create(validCurvePoint);

        // Assert
        assertThat(created).isNotNull();
        assertThat(created.getCurveId()).isEqualTo(1);
        assertThat(created.getCreationDate()).isNotNull();
        assertThat(created.getAsOfDate()).isNotNull();
        verify(curvePointRepository).save(any(CurvePoint.class));
    }

    @Test
    @DisplayName("Should force null ID when creating CurvePoint")
    void shouldForceNullIdWhenCreating() {
        // Arrange
        validCurvePoint.setId(999); // ID existant
        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act
        curvePointService.create(validCurvePoint);

        // Assert
        verify(curvePointRepository).save(argThat(cp -> cp.getId() == null));
    }

    @Test
    @DisplayName("Should set asOfDate to now if not provided")
    void shouldSetAsOfDateToNowIfNotProvided() {
        // Arrange
        validCurvePoint.setAsOfDate(null);
        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act
        curvePointService.create(validCurvePoint);

        // Assert
        verify(curvePointRepository).save(argThat(cp -> cp.getAsOfDate() != null));
    }

    @Test
    @DisplayName("Should throw exception when creating null CurvePoint")
    void shouldThrowExceptionWhenCreatingNull() {
        // Act & Assert
        assertThatThrownBy(() -> curvePointService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }


    @Test
    @DisplayName("Should update CurvePoint successfully")
    void shouldUpdateCurvePointSuccessfully() {
        // Arrange
        when(curvePointRepository.existsById(1)).thenReturn(true);
        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act
        CurvePoint updated = curvePointService.update(1, validCurvePoint);

        // Assert
        assertThat(updated).isNotNull();
        verify(curvePointRepository).existsById(1);
        verify(curvePointRepository).save(any(CurvePoint.class));
    }

    @Test
    @DisplayName("Should force ID when updating CurvePoint")
    void shouldForceIdWhenUpdating() {
        // Arrange
        validCurvePoint.setId(999); 
        when(curvePointRepository.existsById(1)).thenReturn(true);
        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(validCurvePoint);

        // Act
        curvePointService.update(1, validCurvePoint);

        // Assert
        verify(curvePointRepository).save(argThat(cp -> cp.getId().equals(1)));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent CurvePoint")
    void shouldThrowExceptionWhenUpdatingNonExistent() {
        // Arrange
        when(curvePointRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> curvePointService.update(999, validCurvePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid ID")
    void shouldThrowExceptionWhenUpdatingWithInvalidId() {
        // Act & Assert
        assertThatThrownBy(() -> curvePointService.update(null, validCurvePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> curvePointService.update(0, validCurvePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> curvePointService.update(-1, validCurvePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }

    @Test
    @DisplayName("Should find all CurvePoints")
    void shouldFindAllCurvePoints() {
        // Arrange
        CurvePoint anotherCurvePoint = new CurvePoint();
        anotherCurvePoint.setCurveId(2);
        anotherCurvePoint.setTerm(new java.math.BigDecimal("2.0"));
        anotherCurvePoint.setValue(new java.math.BigDecimal("3.0"));
        List<CurvePoint> curvePoints = Arrays.asList(
            validCurvePoint,
            anotherCurvePoint
        );
        when(curvePointRepository.findAll()).thenReturn(curvePoints);

        // Act
        List<CurvePoint> result = curvePointService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        verify(curvePointRepository).findAll();
    }

    @Test
    @DisplayName("Should find CurvePoint by ID")
    void shouldFindCurvePointById() {
        // Arrange
        when(curvePointRepository.findById(1)).thenReturn(Optional.of(validCurvePoint));

        // Act
        Optional<CurvePoint> result = curvePointService.findById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getCurveId()).isEqualTo(1);
        verify(curvePointRepository).findById(1);
    }

    @Test
    @DisplayName("Should return empty when finding by invalid ID")
    void shouldReturnEmptyWhenFindingByInvalidId() {
        // Act
        Optional<CurvePoint> result1 = curvePointService.findById(null);
        Optional<CurvePoint> result2 = curvePointService.findById(0);
        Optional<CurvePoint> result3 = curvePointService.findById(-1);

        // Assert
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
        assertThat(result3).isEmpty();
        verify(curvePointRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should return empty when CurvePoint not found")
    void shouldReturnEmptyWhenCurvePointNotFound() {
        // Arrange
        when(curvePointRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<CurvePoint> result = curvePointService.findById(999);

        // Assert
        assertThat(result).isEmpty();
    }

    // DELETE Tests
    @Test
    @DisplayName("Should delete CurvePoint successfully")
    void shouldDeleteCurvePointSuccessfully() {
        // Arrange
        doNothing().when(curvePointRepository).deleteById(1);

        // Act
        curvePointService.deleteById(1);

        // Assert
        verify(curvePointRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent CurvePoint")
    void shouldThrowExceptionWhenDeletingNonExistent() {
        // Arrange
        doThrow(new EmptyResultDataAccessException(1)).when(curvePointRepository).deleteById(999);

        // Act & Assert
        assertThatThrownBy(() -> curvePointService.deleteById(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when deleting with invalid ID")
    void shouldThrowExceptionWhenDeletingWithInvalidId() {
        // Act & Assert
        assertThatThrownBy(() -> curvePointService.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> curvePointService.deleteById(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> curvePointService.deleteById(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }
}