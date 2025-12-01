package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
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
@DisplayName("TradeService Tests")
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeService tradeService;

    private Trade validTrade;

    @BeforeEach
    void setUp() {
        validTrade = new Trade("TestAccount", "BUY", 100.0, null);
        validTrade.setBuyPrice(50.0);
        validTrade.setTradeId(1);
    }

    @Test
    @DisplayName("Should create Trade successfully")
    void shouldCreateTradeSuccessfully() {
        // Arrange
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        // Act
        Trade created = tradeService.create(validTrade);

        // Assert
        assertThat(created).isNotNull();
        assertThat(created.getAccount()).isEqualTo("TestAccount");
        assertThat(created.getCreationDate()).isNotNull();
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    @DisplayName("Should force null ID when creating Trade")
    void shouldForceNullIdWhenCreating() {
        // Arrange
        validTrade.setTradeId(999); // ID existant
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        // Act
        tradeService.create(validTrade);

        // Assert
        verify(tradeRepository).save(argThat(trade -> trade.getTradeId() == null));
    }

    @Test
    @DisplayName("Should throw exception when creating null Trade")
    void shouldThrowExceptionWhenCreatingNull() {
        // Act & Assert
        assertThatThrownBy(() -> tradeService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Should auto-generate creationDate when creating Trade")
    void shouldAutoGenerateCreationDate() {
        // Arrange
        validTrade.setCreationDate(null);
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        // Act
        tradeService.create(validTrade);

        // Assert
        verify(tradeRepository).save(argThat(trade -> trade.getCreationDate() != null));
    }

    @Test
    @DisplayName("Should auto-generate tradeDate when creating Trade")
    void shouldAutoGenerateTradeDate() {
        // Arrange
        validTrade.setTradeDate(null);
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        // Act
        tradeService.create(validTrade);

        // Assert
        verify(tradeRepository).save(argThat(trade -> trade.getTradeDate() != null));
    }

    @Test
    @DisplayName("Should update Trade successfully")
    void shouldUpdateTradeSuccessfully() {
        // Arrange
        when(tradeRepository.existsById(1)).thenReturn(true);
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        // Act
        Trade updated = tradeService.update(1, validTrade);

        // Assert
        assertThat(updated).isNotNull();
        assertThat(updated.getRevisionDate()).isNotNull();
        verify(tradeRepository).existsById(1);
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    @DisplayName("Should force ID when updating Trade")
    void shouldForceIdWhenUpdating() {
        // Arrange
        validTrade.setTradeId(999); // ID différent
        when(tradeRepository.existsById(1)).thenReturn(true);
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        // Act
        tradeService.update(1, validTrade);

        // Assert
        verify(tradeRepository).save(argThat(trade -> trade.getTradeId().equals(1)));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent Trade")
    void shouldThrowExceptionWhenUpdatingNonExistent() {
        // Arrange
        when(tradeRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> tradeService.update(999, validTrade))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid ID")
    void shouldThrowExceptionWhenUpdatingWithInvalidId() {
        // Act & Assert
        assertThatThrownBy(() -> tradeService.update(null, validTrade))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> tradeService.update(0, validTrade))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> tradeService.update(-1, validTrade))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }

    @Test
    @DisplayName("Should find all Trades")
    void shouldFindAllTrades() {
        // Arrange
        List<Trade> trades = Arrays.asList(validTrade, new Trade("Account2", "SELL", null, 200.0));
        when(tradeRepository.findAll()).thenReturn(trades);

        // Act
        List<Trade> result = tradeService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        verify(tradeRepository).findAll();
    }

    @Test
    @DisplayName("Should find Trade by ID")
    void shouldFindTradeById() {
        // Arrange
        when(tradeRepository.findById(1)).thenReturn(Optional.of(validTrade));

        // Act
        Optional<Trade> result = tradeService.findById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getAccount()).isEqualTo("TestAccount");
        verify(tradeRepository).findById(1);
    }

    @Test
    @DisplayName("Should return empty when finding by invalid ID")
    void shouldReturnEmptyWhenFindingByInvalidId() {
        // Act
        Optional<Trade> result1 = tradeService.findById(null);
        Optional<Trade> result2 = tradeService.findById(0);
        Optional<Trade> result3 = tradeService.findById(-1);

        // Assert
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
        assertThat(result3).isEmpty();
        verify(tradeRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should return empty when Trade not found")
    void shouldReturnEmptyWhenTradeNotFound() {
        // Arrange
        when(tradeRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Trade> result = tradeService.findById(999);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete Trade successfully")
    void shouldDeleteTradeSuccessfully() {
        // Arrange
        doNothing().when(tradeRepository).deleteById(1);

        // Act
        tradeService.deleteById(1);

        // Assert
        verify(tradeRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent Trade")
    void shouldThrowExceptionWhenDeletingNonExistent() {
        // Arrange
        doThrow(new EmptyResultDataAccessException(1)).when(tradeRepository).deleteById(999);

        // Act & Assert
        assertThatThrownBy(() -> tradeService.deleteById(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should throw exception when deleting with invalid ID")
    void shouldThrowExceptionWhenDeletingWithInvalidId() {
        // Act & Assert
        assertThatThrownBy(() -> tradeService.deleteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> tradeService.deleteById(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");

        assertThatThrownBy(() -> tradeService.deleteById(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID");
    }
}