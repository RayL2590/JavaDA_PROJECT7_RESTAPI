package com.nnk.springboot.validation; 
import jakarta.validation.Constraint; 
import jakarta.validation.Payload; 
import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Indique que l'annotation s'applique aux classes
@Retention(RetentionPolicy.RUNTIME) // L'annotation est disponible à l'exécution
@Constraint(validatedBy = AtLeastOneRatingPresentValidator.class)
@Documented
public @interface AtLeastOneRatingPresent { 
    String message() default "At least one rating (Moody's, S&P, or Fitch) must be provided"; 
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}