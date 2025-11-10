package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
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
@DisplayName("RuleNameService Tests")
class RuleNameServiceTest {

    @Mock
    private RuleNameRepository ruleNameRepository;

    @InjectMocks
    private RuleNameService ruleNameService;

    private RuleName validRuleName;

    @BeforeEach
    void setUp() {
        validRuleName = new RuleName("TestRule", "TestDescription");
        validRuleName.setId(1);
    }

    @Test
    @DisplayName("Should create RuleName successfully")
    void shouldCreateRuleNameSuccessfully() {
        // Arrange
        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(validRuleName);

        // Act
        RuleName created = ruleNameService.create(validRuleName);

        // Assert
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("TestRule");
        verify(ruleNameRepository).save(any(RuleName.class));
    }

    @Test
    @DisplayName("Should force null ID when creating RuleName")
    void shouldForceNullIdWhenCreating() {
        // Arrange
        validRuleName.setId(999); // ID existant
        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(validRuleName);

        // Act
        ruleNameService.create(validRuleName);

        // Assert
        verify(ruleNameRepository).save(argThat(ruleName -> ruleName.getId() == null));
    }

    @Test
    @DisplayName("Should throw exception when creating null RuleName")
    void shouldThrowExceptionWhenCreatingNull() {
        // Act & Assert
        assertThatThrownBy(() -> ruleNameService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    // UPDATE Tests
    @Test
    @DisplayName("Should update RuleName successfully")
    void shouldUpdateRuleNameSuccessfully() {
        // Arrange
        when(ruleNameRepository.existsById(1)).thenReturn(true);
        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(validRuleName);

        // Act
        RuleName updated = ruleNameService.update(1, validRuleName);

        // Assert
        assertThat(updated).isNotNull();
        verify(ruleNameRepository).existsById(1);
        verify(ruleNameRepository).save(any(RuleName.class));
    }

    @Test
    @DisplayName("Should force ID when updating RuleName")
    void shouldForceIdWhenUpdating() {
        // Arrange
        validRuleName.setId(999); // ID différent
        when(ruleNameRepository.existsById(1)).thenReturn(true);
        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(validRuleName);

        // Act
        ruleNameService.update(1, validRuleName);

        // Assert
        verify(ruleNameRepository).save(argThat(ruleName -> ruleName.getId().equals(1)));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent RuleName")
    void shouldThrowExceptionWhenUpdatingNonExistent() {
        // Arrange
        when(ruleNameRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> ruleNameService.update(999, validRuleName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid ID")
    void shouldThrowExceptionWhenUpdatingWithInvalidId() {
        // Act & Assert
        assertThatThrownBy(() -> ruleNameService.update(null, validRuleName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> ruleNameService.update(0, validRuleName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> ruleNameService.update(-1, validRuleName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }

    @Test
    @DisplayName("Should find all RuleNames")
    void shouldFindAllRuleNames() {
        // Arrange
        List<RuleName> ruleNames = Arrays.asList(validRuleName, new RuleName("Rule2", "Description2"));
        when(ruleNameRepository.findAll()).thenReturn(ruleNames);

        // Act
        List<RuleName> result = ruleNameService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        verify(ruleNameRepository).findAll();
    }

    @Test
    @DisplayName("Should find RuleName by ID")
    void shouldFindRuleNameById() {
        // Arrange
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(validRuleName));

        // Act
        Optional<RuleName> result = ruleNameService.findById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("TestRule");
        verify(ruleNameRepository).findById(1);
    }

    @Test
    @DisplayName("Should return empty when finding by invalid ID")
    void shouldReturnEmptyWhenFindingByInvalidId() {
        // Act
        Optional<RuleName> result1 = ruleNameService.findById(null);
        Optional<RuleName> result2 = ruleNameService.findById(0);
        Optional<RuleName> result3 = ruleNameService.findById(-1);

        // Assert
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
        assertThat(result3).isEmpty();
        verify(ruleNameRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should return empty when RuleName not found")
    void shouldReturnEmptyWhenRuleNameNotFound() {
        // Arrange
        when(ruleNameRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<RuleName> result = ruleNameService.findById(999);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete RuleName successfully")
    void shouldDeleteRuleNameSuccessfully() {
        // Arrange
        doNothing().when(ruleNameRepository).deleteById(1);

        // Act
        ruleNameService.deleteById(1);

        // Assert
        verify(ruleNameRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent RuleName")
    void shouldThrowExceptionWhenDeletingNonExistent() {
        // Arrange
        doThrow(new EmptyResultDataAccessException(1)).when(ruleNameRepository).deleteById(999);

        // Act & Assert
        assertThatThrownBy(() -> ruleNameService.deleteById(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when deleting with invalid ID")
    void shouldThrowExceptionWhenDeletingWithInvalidId() {
        // Act & Assert
        assertThatThrownBy(() -> ruleNameService.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> ruleNameService.deleteById(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> ruleNameService.deleteById(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }
}