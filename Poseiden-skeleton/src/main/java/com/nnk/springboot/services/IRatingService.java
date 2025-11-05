package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface IRatingService {

    List<Rating> findAll();

    Optional<Rating> findById(@NotNull Integer id);

    Rating create(@Valid Rating rating);

    Rating update(@NotNull Integer id, @Valid Rating rating);

    void deleteById(@NotNull Integer id);
}