package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

public interface ICurvePointService {

    List<CurvePoint> findAll();

    Optional<CurvePoint> findById(@NotNull Integer id);

    CurvePoint create(@Valid CurvePoint curvePoint);

    CurvePoint update(@NotNull Integer id, @Valid CurvePoint curvePoint);

    void deleteById(Integer id, UserDetails userDetails);
}