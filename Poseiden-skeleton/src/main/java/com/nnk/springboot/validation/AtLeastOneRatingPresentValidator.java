package com.nnk.springboot.validation;

import com.nnk.springboot.domain.Rating;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneRatingPresentValidator implements ConstraintValidator<AtLeastOneRatingPresent, Rating> {

    @Override
    public boolean isValid(Rating rating, ConstraintValidatorContext context) {
        if (rating == null) {
            return true;
        }

        boolean hasMoodys = rating.getMoodysRating() != null && !rating.getMoodysRating().trim().isEmpty();
        boolean hasSP = rating.getSandPRating() != null && !rating.getSandPRating().trim().isEmpty();
        boolean hasFitch = rating.getFitchRating() != null && !rating.getFitchRating().trim().isEmpty();

        return hasMoodys || hasSP || hasFitch;
    }
}