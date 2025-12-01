package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface ITradeService {

    List<Trade> findAll();

    Optional<Trade> findById(@NotNull Integer id);

    Trade create(@Valid Trade trade);

    Trade update(@NotNull Integer id, @Valid Trade trade);

    void deleteById(@NotNull Integer id);
}