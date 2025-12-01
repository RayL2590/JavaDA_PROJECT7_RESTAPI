package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class TradeService implements ITradeService {

    private final TradeRepository tradeRepository;

    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findAll() {
        return tradeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trade> findById(@NotNull Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return tradeRepository.findById(id);
    }

    @Override
    public Trade create(@Valid Trade trade) {
        if (trade == null) {
            throw new IllegalArgumentException("Trade cannot be null");
        }
        trade.setTradeId(null);

        LocalDateTime now = LocalDateTime.now();
        if (trade.getCreationDate() == null) {
            trade.setCreationDate(now);
        }
        if (trade.getTradeDate() == null) {
            trade.setTradeDate(now);
        }

        return tradeRepository.save(trade);
    }

    @Override
    public Trade update(@NotNull Integer id, @Valid Trade trade) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
        if (!tradeRepository.existsById(id)) {
            throw new IllegalArgumentException("Trade not found with id: " + id);
        }
        trade.setTradeId(id);

        trade.setRevisionDate(LocalDateTime.now());

        return tradeRepository.save(trade);
    }

    @Override
    public void deleteById(@NotNull Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
        try {
            tradeRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Trade not found with id: " + id);
        }
    }
}