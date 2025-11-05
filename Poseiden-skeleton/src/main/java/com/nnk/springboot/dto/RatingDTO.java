package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {

    private Integer id;

    @Pattern(regexp = "^(Aaa|Aa[1-3]|A[1-3]|Baa[1-3]|Ba[1-3]|B[1-3]|Caa[1-3]|Ca|C)?$",
            message = "Moody's rating must follow standard format (e.g., Aaa, Aa1, A2, Baa3, Ba1, B2, Caa1, Ca, C)")
    @Size(max = 125, message = "Moody's rating must be less than 125 characters")
    private String moodysRating;

    @Pattern(regexp = "^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|B[+-]?|CCC[+-]?|CC|C|D)?$",
            message = "S&P rating must follow standard format (e.g., AAA, AA+, A-, BBB, BB+, B-, CCC, D)")
    @Size(max = 125, message = "S&P rating must be less than 125 characters")
    private String sandPRating;

    @Pattern(regexp = "^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|B[+-]?|CCC[+-]?|CC|C|D)?$",
            message = "Fitch rating must follow standard format (e.g., AAA, AA+, A-, BBB, BB+, B-, CCC, D)")
    @Size(max = 125, message = "Fitch rating must be less than 125 characters")
    private String fitchRating;

    @Min(value = 1, message = "Order number must be positive")
    private Integer orderNumber;

    public RatingDTO(String moodysRating, String sandPRating, String fitchRating) {
        this.moodysRating = moodysRating;
        this.sandPRating = sandPRating;
        this.fitchRating = fitchRating;
    }

    public RatingDTO(String moodysRating, String sandPRating, String fitchRating, Integer orderNumber) {
        this.moodysRating = moodysRating;
        this.sandPRating = sandPRating;
        this.fitchRating = fitchRating;
        this.orderNumber = orderNumber;
    }

    public boolean isInvestmentGrade() {
        return isInvestmentGradeMoodys(moodysRating) ||
                isInvestmentGradeSP(sandPRating) ||
                isInvestmentGradeFitch(fitchRating);
    }

    private boolean isInvestmentGradeMoodys(String rating) {
        if (rating == null) return false;
        return rating.matches("^(Aaa|Aa[1-3]|A[1-3]|Baa[1-3])$");
    }

    private boolean isInvestmentGradeSP(String rating) {
        if (rating == null) return false;
        return rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?)$");
    }

    private boolean isInvestmentGradeFitch(String rating) {
        if (rating == null) return false;
        return rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?)$");
    }
}