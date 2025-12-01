package com.nnk.springboot.validation;

import com.nnk.springboot.domain.Trade;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TradeValidator implements ConstraintValidator<ValidTradeData, Trade> {

    @Override
    public void initialize(ValidTradeData constraintAnnotation) {
    }

    @Override
    public boolean isValid(Trade trade, ConstraintValidatorContext context) {
        if (trade == null) {
            return true;
        }

        // Règle métier : Au moins une opération (achat OU vente) doit être définie
        boolean hasBuyOperation = trade.getBuyQuantity() != null && trade.getBuyQuantity() > 0;
        boolean hasSellOperation = trade.getSellQuantity() != null && trade.getSellQuantity() > 0;

        if (!hasBuyOperation && !hasSellOperation) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "At least one operation (buy or sell) with positive quantity must be defined"
            ).addConstraintViolation();
            return false;
        }

        // Validation cohérence achat : si buyQuantity alors buyPrice obligatoire
        if (hasBuyOperation && (trade.getBuyPrice() == null || trade.getBuyPrice() <= 0)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Buy price must be positive when buy quantity is specified"
            ).addConstraintViolation();
            return false;
        }

        // Validation cohérence vente : si sellQuantity alors sellPrice obligatoire
        if (hasSellOperation && (trade.getSellPrice() == null || trade.getSellPrice() <= 0)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Sell price must be positive when sell quantity is specified"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}