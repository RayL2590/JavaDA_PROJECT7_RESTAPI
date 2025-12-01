package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.ITradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(TradeController.class)
@DisplayName("TradeController Tests")
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITradeService tradeService;

    private Trade validTrade;
    private List<Trade> trades;

    @BeforeEach
    void setUp() {
        validTrade = new Trade("TestAccount", "BUY", 100.0, null);
        validTrade.setBuyPrice(50.0);
        validTrade.setTradeId(1);

        Trade trade2 = new Trade("Account2", "SELL", null, 200.0);
        trade2.setSellPrice(75.0);
        trade2.setTradeId(2);

        trades = Arrays.asList(validTrade, trade2);
    }

        @Test
        @WithMockUser
        @DisplayName("GET /trade/list should display all Trades")
        void shouldDisplayAllTrades() throws Exception {
        // Arrange
        when(tradeService.findAll()).thenReturn(trades);

        // Act & Assert
        mockMvc.perform(get("/trade/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/list"))
                .andExpect(model().attributeExists("trades"))
                .andExpect(model().attribute("trades", hasSize(2)));

        verify(tradeService).findAll();
    }

        @Test
        @WithMockUser
        @DisplayName("GET /trade/list should display empty list when no Trades")
        void shouldDisplayEmptyListWhenNoTrades() throws Exception {
        // Arrange
        when(tradeService.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/trade/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/list"))
                .andExpect(model().attribute("trades", hasSize(0)));
    }

    // ADD Tests
        @Test
        @WithMockUser
        @DisplayName("GET /trade/add should display add form")
        void shouldDisplayAddForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/trade/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(model().attributeExists("trade"));
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/validate should create Trade and redirect")
        void shouldCreateTradeAndRedirect() throws Exception {
        // Arrange
        when(tradeService.create(any(Trade.class))).thenReturn(validTrade);

        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(tradeService).create(any(Trade.class));
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/validate should return to form with validation errors")
        void shouldReturnToFormWithValidationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", "")
                .param("type", "")
                .param("buyQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(model().attributeHasErrors("trade"));

        verify(tradeService, never()).create(any(Trade.class));
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/validate should handle negative buyQuantity")
        void shouldHandleNegativeBuyQuantity() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "BUY")
                .param("buyQuantity", "-100.0")
                .param("buyPrice", "50.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(model().attributeHasErrors("trade"));
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/validate should handle service exception")
        void shouldHandleServiceExceptionOnCreate() throws Exception {
        // Arrange
        when(tradeService.create(any(Trade.class)))
                .thenThrow(new IllegalArgumentException("Test error"));

        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "50.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(model().attributeExists("errorMessage"));
    }

        @Test
        @WithMockUser
        @DisplayName("GET /trade/update/{id} should display update form")
        void shouldDisplayUpdateForm() throws Exception {
        // Arrange
        when(tradeService.findById(1)).thenReturn(Optional.of(validTrade));

        // Act & Assert
        mockMvc.perform(get("/trade/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(model().attributeExists("trade"))
                .andExpect(model().attribute("trade", hasProperty("account", is("TestAccount"))));

        verify(tradeService).findById(1);
    }

        @Test
        @WithMockUser
        @DisplayName("GET /trade/update/{id} should redirect when Trade not found")
        void shouldRedirectWhenTradeNotFoundForUpdate() throws Exception {
        // Arrange
        when(tradeService.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/trade/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/update/{id} should update Trade and redirect")
        void shouldUpdateTradeAndRedirect() throws Exception {
        // Arrange
        when(tradeService.update(eq(1), any(Trade.class))).thenReturn(validTrade);

        // Act & Assert
        mockMvc.perform(post("/trade/update/1")
                .with(csrf())
                .param("account", "UpdatedAccount")
                .param("type", "SELL")
                .param("sellQuantity", "150.0")
                .param("sellPrice", "75.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(tradeService).update(eq(1), any(Trade.class));
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/update/{id} should return to form with validation errors")
        void shouldReturnToUpdateFormWithValidationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/trade/update/1")
                .with(csrf())
                .param("account", "")
                .param("type", "")
                .param("buyQuantity", "150.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(model().attributeHasErrors("trade"));

        verify(tradeService, never()).update(any(), any());
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/update/{id} should handle service exception")
        void shouldHandleServiceExceptionOnUpdate() throws Exception {
        // Arrange
        when(tradeService.update(eq(999), any(Trade.class)))
                .thenThrow(new IllegalArgumentException("Trade not found"));

        // Act & Assert
        mockMvc.perform(post("/trade/update/999")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "50.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // DELETE Tests
        @Test
        @WithMockUser
        @DisplayName("POST /trade/delete/{id} should delete Trade and redirect")
        void shouldDeleteTradeAndRedirect() throws Exception {
        // Arrange
        doNothing().when(tradeService).deleteById(1);

        // Act & Assert
        mockMvc.perform(post("/trade/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(tradeService).deleteById(1);
    }

        @Test
        @WithMockUser
        @DisplayName("POST /trade/delete/{id} should handle non-existent Trade")
        void shouldHandleDeleteNonExistentTrade() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Trade not found"))
                .when(tradeService).deleteById(999);

        // Act & Assert
        mockMvc.perform(post("/trade/delete/999").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

        @Test
        @WithMockUser
        @DisplayName("Should use flash attributes for success messages")
        void shouldUseFlashAttributesForSuccess() throws Exception {
        // Arrange
        when(tradeService.create(any(Trade.class))).thenReturn(validTrade);

        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "50.0"))
                .andExpect(flash().attribute("successMessage", notNullValue()));
    }

        @Test
        @WithMockUser
        @DisplayName("Should use flash attributes for error messages")
        void shouldUseFlashAttributesForErrors() throws Exception {
        // Arrange
        when(tradeService.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/trade/update/999"))
                .andExpect(flash().attribute("errorMessage", notNullValue()));
    }

    // Edge Cases
        @Test
        @WithMockUser
        @DisplayName("Should handle very long account name")
        void shouldHandleVeryLongAccountName() throws Exception {
        // Arrange
        String longAccount = "a".repeat(31);

        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", longAccount)
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "50.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(model().attributeHasErrors("trade"));
    }

        @Test
        @WithMockUser
        @DisplayName("Should handle null optional fields")
        void shouldHandleNullOptionalFields() throws Exception {
        // Arrange
        when(tradeService.create(any(Trade.class))).thenReturn(validTrade);

        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "50.0"))
                .andExpect(status().is3xxRedirection());
    }

        @Test
        @WithMockUser
        @DisplayName("Should accept trade with both buy and sell operations")
        void shouldAcceptTradeWithBothOperations() throws Exception {
        // Arrange
        when(tradeService.create(any(Trade.class))).thenReturn(validTrade);

        // Act & Assert
        mockMvc.perform(post("/trade/validate")
                .with(csrf())
                .param("account", "TestAccount")
                .param("type", "SWAP")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "50.0")
                .param("sellQuantity", "100.0")
                .param("sellPrice", "50.0"))
                .andExpect(status().is3xxRedirection());
    }
}