package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class RuleNameService implements IRuleNameService {

    private final RuleNameRepository ruleNameRepository;

    public RuleNameService(RuleNameRepository ruleNameRepository) {
        this.ruleNameRepository = ruleNameRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleName> findAll() {
        return ruleNameRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RuleName> findById(@NotNull Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return ruleNameRepository.findById(id);
    }

    @Override
    public RuleName create(@Valid RuleName ruleName) {
        if (ruleName == null) {
            throw new IllegalArgumentException("RuleName cannot be null");
        }
        ruleName.setId(null);
        return ruleNameRepository.save(ruleName);
    }

    @Override
    public RuleName update(@NotNull Integer id, @Valid RuleName ruleName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
        if (!ruleNameRepository.existsById(id)) {
            throw new IllegalArgumentException("RuleName not found with id: " + id);
        }
        ruleName.setId(id);
        return ruleNameRepository.save(ruleName);
    }

    @Override
    public void deleteById(@NotNull Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
        try {
            ruleNameRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("RuleName not found with id: " + id);
        }
    }
}