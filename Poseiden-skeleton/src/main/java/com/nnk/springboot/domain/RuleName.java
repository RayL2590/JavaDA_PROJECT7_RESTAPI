package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rule_name")
public class RuleName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 125, message = "Name must be less than 125 characters")
    @Column(name = "name", length = 125)
    private String name;

    @Size(max = 125, message = "Description must be less than 125 characters")
    @Column(name = "description", length = 125)
    private String description;

    @Size(max = 125, message = "JSON must be less than 125 characters")
    @Column(name = "json", length = 125)
    private String json;

    @Size(max = 512, message = "Template must be less than 512 characters")
    @Column(name = "template", length = 512)
    private String template;

    @Size(max = 125, message = "SQL string must be less than 125 characters")
    @Column(name = "sqlStr", length = 125)
    private String sqlStr;

    @Size(max = 125, message = "SQL part must be less than 125 characters")
    @Column(name = "sqlPart", length = 125)
    private String sqlPart;

    @Version
    private Long version;

    public RuleName(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleName)) return false;
        RuleName ruleName = (RuleName) o;
        return id != null && id.equals(ruleName.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}