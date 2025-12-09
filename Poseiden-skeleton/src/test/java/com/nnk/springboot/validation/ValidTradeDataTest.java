package com.nnk.springboot.validation;

import com.nnk.springboot.domain.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidTradeDataTest {

    private TradeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new TradeValidator();
        context = mock(ConstraintValidatorContext.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));
    }

    @Test
    void validTrade_withBuyQuantity_shouldReturnTrue() {
        Trade trade = new Trade();
        trade.setBuyQuantity(100.0); 
        trade.setSellQuantity(null);
        assertTrue(validator.isValid(trade, context));
    }

    @Test
    void validTrade_withSellQuantity_shouldReturnTrue() {
        Trade trade = new Trade();
        trade.setBuyQuantity(null);
        trade.setSellQuantity(50.0); 
        assertTrue(validator.isValid(trade, context));
    }

    @Test
    void validTrade_withBothQuantities_shouldReturnTrue() {
        Trade trade = new Trade();
        trade.setBuyQuantity(100.0); 
        trade.setSellQuantity(50.0); 
        assertTrue(validator.isValid(trade, context));
    }

    @Test
    void invalidTrade_withNoQuantities_shouldReturnFalse() {
        Trade trade = new Trade();
        trade.setBuyQuantity(null);
        trade.setSellQuantity(null);
        assertFalse(validator.isValid(trade, context));
    }

    @Test
    void invalidTrade_withZeroQuantities_shouldReturnFalse() {
        Trade trade = new Trade();
        trade.setBuyQuantity(0.0);
        trade.setSellQuantity(0.0);
        assertFalse(validator.isValid(trade, context));
    }

    @Test
    void validTrade_withBuyQuantityPositiveSellQuantityZero_shouldReturnTrue() {
        Trade trade = new Trade();
        trade.setBuyQuantity(10.0); 
        trade.setSellQuantity(0.0);
        assertTrue(validator.isValid(trade, context));
    }

    @Test
    void validTrade_withSellQuantityPositiveBuyQuantityZero_shouldReturnTrue() {
        Trade trade = new Trade();
        trade.setBuyQuantity(0.0);
        trade.setSellQuantity(20.0); 
        assertTrue(validator.isValid(trade, context));
    }
}
