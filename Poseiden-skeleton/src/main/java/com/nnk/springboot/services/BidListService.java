package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class BidListService implements IBidListService {

    private final BidListRepository bidListRepository;

    public BidListService(BidListRepository bidListRepository) {
        this.bidListRepository = bidListRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidList> findAll() {
        return bidListRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BidList> findById(@NotNull Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return bidListRepository.findById(id);
    }

    @Override
    public BidList create(@Valid BidList bidList) {
        if (bidList == null) {
            throw new IllegalArgumentException("BidList cannot be null");
        }
        bidList.setBidListId(null);
        bidList.setCreationDate(LocalDateTime.now());
        return bidListRepository.save(bidList);
    }

    @Override
    public BidList update(@NotNull Integer id, @Valid BidList bidList) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
        if (!bidListRepository.existsById(id)) {
            throw new IllegalArgumentException("BidList not found with id: " + id);
        }
        bidList.setBidListId(id);
        bidList.setRevisionDate(LocalDateTime.now());
        return bidListRepository.save(bidList);
    }

    @Override
    public void deleteById(Integer id, org.springframework.security.core.userdetails.UserDetails userDetails) {
    if (id == null || id <= 0) {
        throw new IllegalArgumentException("Invalid ID: " + id);
    }

    BidList bidList = bidListRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("BidList not found with id: " + id));

    String currentUsername = userDetails.getUsername();
    boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    boolean isOwner = bidList.getCreationName() != null && bidList.getCreationName().equals(currentUsername);

    if (!isOwner && !isAdmin) {
        throw new AccessDeniedException("You are not authorized to delete this BidList");
    }

    bidListRepository.delete(bidList);
    }
}