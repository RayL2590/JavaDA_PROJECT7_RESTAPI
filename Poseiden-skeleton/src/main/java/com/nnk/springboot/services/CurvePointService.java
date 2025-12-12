package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class CurvePointService implements ICurvePointService {

    private final CurvePointRepository curvePointRepository;

    public CurvePointService(CurvePointRepository curvePointRepository) {
        this.curvePointRepository = curvePointRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurvePoint> findAll() {
        return curvePointRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurvePoint> findById(@NotNull Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return curvePointRepository.findById(id);
    }

    @Override
    public CurvePoint create(@Valid CurvePoint curvePoint) {
        if (curvePoint == null) {
            throw new IllegalArgumentException("CurvePoint cannot be null");
        }
        curvePoint.setId(null);
        curvePoint.setCreationDate(LocalDateTime.now());

        if (curvePoint.getAsOfDate() == null) {
            curvePoint.setAsOfDate(LocalDateTime.now());
        }

        return curvePointRepository.save(curvePoint);
    }

    @Override
    public CurvePoint update(@NotNull Integer id, @Valid CurvePoint curvePoint) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
        if (!curvePointRepository.existsById(id)) {
            throw new IllegalArgumentException("CurvePoint not found with id: " + id);
        }
        curvePoint.setId(id);
        return curvePointRepository.save(curvePoint);
    }

    @Override
    public void deleteById(Integer id, UserDetails userDetails) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }

        CurvePoint curvePoint = curvePointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CurvePoint not found with id: " + id));

        String currentUsername = userDetails.getUsername();
        
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = curvePoint.getCreationName() != null && 
                        curvePoint.getCreationName().equals(currentUsername);

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this CurvePoint");
        }
        try {
            curvePointRepository.delete(curvePoint);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while deleting CurvePoint", e);
        }
    }
}