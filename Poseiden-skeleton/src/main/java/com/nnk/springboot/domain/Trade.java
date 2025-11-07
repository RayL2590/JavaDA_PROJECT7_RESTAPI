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
@Table(name = "trade")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Integer tradeId;

    @NotBlank(message = "Account is mandatory")
    @Size(max = 30, message = "Account must be less than 30 characters")
    @Column(name = "account", nullable = false, length = 30)
    private String account;

    @NotBlank(message = "Type is mandatory")
    @Size(max = 30, message = "Type must be less than 30 characters")
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @DecimalMin(value = "0.01", message = "Buy quantity must be positive")
    @Digits(integer = 10, fraction = 2, message = "Buy quantity must have at most 2 decimal places")
    @Column(name = "buy_quantity")
    private BigDecimal buyQuantity;

    @DecimalMin(value = "0.01", message = "Sell quantity must be positive")
    @Digits(integer = 10, fraction = 2, message = "Sell quantity must have at most 2 decimal places")
    @Column(name = "sell_quantity")
    private BigDecimal sellQuantity;

    @DecimalMin(value = "0.0001", message = "Buy price must be positive")
    @Digits(integer = 10, fraction = 4, message = "Buy price must have at most 4 decimal places")
    @Column(name = "buy_price")
    private BigDecimal buyPrice;

    @DecimalMin(value = "0.0001", message = "Sell price must be positive")
    @Digits(integer = 10, fraction = 4, message = "Sell price must have at most 4 decimal places")
    @Column(name = "sell_price")
    private BigDecimal sellPrice;

    @Column(name = "trade_date")
    private LocalDateTime tradeDate;

    @Size(max = 125, message = "Security must be less than 125 characters")
    @Column(name = "security", length = 125)
    private String security;

    @Size(max = 10, message = "Status must be less than 10 characters")
    @Column(name = "status", length = 10)
    private String status;

    @Size(max = 125, message = "Trader must be less than 125 characters")
    @Column(name = "trader", length = 125)
    private String trader;

    @Size(max = 125, message = "Benchmark must be less than 125 characters")
    @Column(name = "benchmark", length = 125)
    private String benchmark;

    @Size(max = 125, message = "Book must be less than 125 characters")
    @Column(name = "book", length = 125)
    private String book;

    @Size(max = 125, message = "Creation name must be less than 125 characters")
    @Column(name = "creation_name", length = 125)
    private String creationName;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Size(max = 125, message = "Revision name must be less than 125 characters")
    @Column(name = "revision_name", length = 125)
    private String revisionName;

    @Column(name = "revision_date")
    private LocalDateTime revisionDate;

    @Size(max = 125, message = "Deal name must be less than 125 characters")
    @Column(name = "deal_name", length = 125)
    private String dealName;

    @Size(max = 125, message = "Deal type must be less than 125 characters")
    @Column(name = "deal_type", length = 125)
    private String dealType;

    @Size(max = 125, message = "Source list ID must be less than 125 characters")
    @Column(name = "source_list_id", length = 125)
    private String sourceListId;

    @Size(max = 125, message = "Side must be less than 125 characters")
    @Column(name = "side", length = 125)
    private String side;

    @Version
    private Long version;

    public Trade(String account, String type) {
        this.account = account;
        this.type = type;
    }

    public Trade(String account, String type, BigDecimal buyQuantity, BigDecimal sellQuantity) {
        this.account = account;
        this.type = type;
        this.buyQuantity = buyQuantity;
        this.sellQuantity = sellQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trade)) return false;
        Trade trade = (Trade) o;
        return tradeId != null && tradeId.equals(trade.tradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId);
    }
}