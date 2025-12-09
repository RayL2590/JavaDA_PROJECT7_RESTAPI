package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

public interface IBidListService {

    List<BidList> findAll();

    Optional<BidList> findById(@NotNull Integer id);

    BidList create(@Valid BidList bidList);

    BidList update(@NotNull Integer id, @Valid BidList bidList);

    void deleteById(Integer id, UserDetails userDetails);
}