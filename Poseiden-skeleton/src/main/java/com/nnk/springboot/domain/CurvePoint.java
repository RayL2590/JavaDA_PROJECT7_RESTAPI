package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "curve_point")
public class CurvePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @NotNull(message = "Curve ID is mandatory")
    @Min(value = 1, message = "Curve ID must be positive")
    @Column(name = "CurveId")
    private Integer curveId;

    @Column(name = "asOfDate")
    private LocalDateTime asOfDate;

    @NotNull(message = "Term is mandatory")
    @DecimalMin(value = "0.0", inclusive = true, message = "Term must be positive or zero")
    @Digits(integer = 10, fraction = 4, message = "Term must be a valid number with max 4 decimal places")
    @Column(name = "term")
    private BigDecimal term;

    @NotNull(message = "Value is mandatory")
    @Digits(integer = 10, fraction = 4, message = "Value must be a valid number with max 4 decimal places")
    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    @Column(name = "creationName")
    private String creationName;

    public CurvePoint(Integer curveId, BigDecimal term, BigDecimal value) {
        this.curveId = curveId;
        this.term = term;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurvePoint)) return false;
        CurvePoint that = (CurvePoint) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}