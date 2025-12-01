package com.nnk.springboot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters, contain a capital letter, a digit and a symbol";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
