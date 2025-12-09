package com.nnk.springboot.validation;

import com.nnk.springboot.domain.Trade;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TradeValidator implements ConstraintValidator<ValidTradeData, Trade> {

    @Override
    public boolean isValid(Trade trade, ConstraintValidatorContext context) {
        if (trade == null) {
            return false;
        }

        Double buyQuantity = trade.getBuyQuantity();
        Double sellQuantity = trade.getSellQuantity();

        // Vérifie si au moins une des quantités est présente et positive
        boolean isBuyValid = buyQuantity != null && buyQuantity > 0;
        boolean isSellValid = sellQuantity != null && sellQuantity > 0;

        return isBuyValid || isSellValid;
    }
}