package com.nnk.springboot.domain;

import com.nnk.springboot.validation.AtLeastOneRatingPresent;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rating")
@AtLeastOneRatingPresent
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Pattern(regexp = "^(Aaa|Aa[1-3]|A[1-3]|Baa[1-3]|Ba[1-3]|B[1-3]|Caa[1-3]|Ca|C)?$",
            message = "Moody's rating must follow standard format")
    @Size(max = 125)
    @Column(name = "moodys_rating", length = 125)
    private String moodysRating;

    @Pattern(regexp = "^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|B[+-]?|CCC[+-]?|CC|C|D)?$",
            message = "S&P rating must follow standard format")
    @Size(max = 125)
    @Column(name = "sand_p_rating", length = 125)
    private String sandPRating;

    @Pattern(regexp = "^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|CCC[+-]?|CC|C|D)?$",
            message = "Fitch rating must follow standard format")
    @Size(max = 125)
    @Column(name = "fitch_rating", length = 125)
    private String fitchRating;

    @Min(value = 1, message = "Order number must be positive")
    @Column(name = "order_number")
    private Integer orderNumber;

    

    public Rating(String moodysRating, String sandPRating, String fitchRating) {
        this.moodysRating = moodysRating;
        this.sandPRating = sandPRating;
        this.fitchRating = fitchRating;
    }

    public Integer getOrder() {
        return this.orderNumber;
    }

    public void setOrder(Integer order) {
        this.orderNumber = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating rating = (Rating) o;
        return id != null && id.equals(rating.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}