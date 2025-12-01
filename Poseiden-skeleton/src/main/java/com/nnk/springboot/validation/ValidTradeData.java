package com.nnk.springboot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Contrainte de validation personnalisée pour Trade.
 * Vérifie qu'au moins une opération (achat ou vente) est définie.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TradeValidator.class)
@Documented
public @interface ValidTradeData {
    String message() default "At least one operation (buy or sell) must be defined";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}