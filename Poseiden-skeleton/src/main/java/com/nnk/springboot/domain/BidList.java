package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bid_list")
public class BidList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_list_id")
    private Integer bidListId;

    @NotBlank(message = "Account is mandatory")
    @Size(max = 30, message = "Account must be less than 30 characters")
    @Column(name = "account", nullable = false, length = 30)
    private String account;

    @NotBlank(message = "Type is mandatory")
    @Size(max = 30, message = "Type must be less than 30 characters")
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @NotNull(message = "Bid quantity is mandatory")
    @DecimalMin(value = "0.0", inclusive = true, message = "Bid quantity must be positive or zero")
    @Column(name = "bid_quantity")
    private Double bidQuantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Ask quantity must be positive or zero")
    @Column(name = "ask_quantity")
    private Double askQuantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Bid must be positive or zero")
    @Column(name = "bid")
    private Double bid;

    @DecimalMin(value = "0.0", inclusive = true, message = "Ask must be positive or zero")
    @Column(name = "ask")
    private Double ask;

    @Size(max = 125, message = "Benchmark must be less than 125 characters")
    @Column(name = "benchmark", length = 125)
    private String benchmark;

    @Column(name = "bid_list_date")
    @CreationTimestamp
    private LocalDateTime bidListDate;

    @Size(max = 125, message = "Commentary must be less than 125 characters")
    @Column(name = "commentary", length = 125)
    private String commentary;

    @Size(max = 125, message = "Security must be less than 125 characters")
    @Column(name = "security", length = 125)
    private String security;

    @Size(max = 10, message = "Status must be less than 10 characters")
    @Column(name = "status", length = 10)
    private String status;

    @Size(max = 125, message = "Trader must be less than 125 characters")
    @Column(name = "trader", length = 125)
    private String trader;

    @Size(max = 125, message = "Book must be less than 125 characters")
    @Column(name = "book", length = 125)
    private String book;

    @Size(max = 125, message = "Creation name must be less than 125 characters")
    @Column(name = "creation_name", length = 125)
    private String creationName;

    @Column(name = "creation_date")
    @CreationTimestamp
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

    

    public BidList(String account, String type, Double bidQuantity) {
        this.account = account;
        this.type = type;
        this.bidQuantity = bidQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BidList)) return false;
        BidList bidList = (BidList) o;
        return bidListId != null && bidListId.equals(bidList.bidListId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bidListId);
    }

    public Integer getId() {
        return bidListId;
    }
}