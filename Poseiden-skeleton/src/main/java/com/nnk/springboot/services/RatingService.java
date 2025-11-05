package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Validated
public class RatingService implements IRatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rating> findById(@NotNull Integer id) {
        return ratingRepository.findById(id);
    }

    @Override
    public Rating create(@Valid Rating rating) {
        rating.setId(null);
        return ratingRepository.save(rating);
    }

    @Override
    public Rating update(@NotNull Integer id, @Valid Rating rating) {
        if (!ratingRepository.existsById(id)) {
            throw new IllegalArgumentException("Rating not found with id: " + id);
        }
        rating.setId(id);
        return ratingRepository.save(rating);
    }

    @Override
    public void deleteById(@NotNull Integer id) {
        try {
            ratingRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Rating not found with id: " + id);
        }
    }
}