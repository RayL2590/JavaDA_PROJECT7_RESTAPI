package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface IRuleNameService {

    List<RuleName> findAll();

    Optional<RuleName> findById(@NotNull Integer id);

    RuleName create(@Valid RuleName ruleName);

    RuleName update(@NotNull Integer id, @Valid RuleName ruleName);

    void deleteById(@NotNull Integer id);
}