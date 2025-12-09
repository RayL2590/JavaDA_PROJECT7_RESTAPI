package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.ICurvePointService;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/curvePoint")
public class CurvePointController {

    private static final Logger logger = LoggerFactory.getLogger(CurvePointController.class);
    private final ICurvePointService curvePointService;

    public CurvePointController(ICurvePointService curvePointService) {
        this.curvePointService = curvePointService;
    }

    @GetMapping("/list")
    public String home(Model model, Authentication authentication) {
        List<CurvePoint> curvePoints = curvePointService.findAll();
        model.addAttribute("curvePoints", curvePoints);
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute("currentRoles", authentication.getAuthorities());
        return "curvePoint/list";
    }

    @GetMapping("/add")
    public String addCurvePointForm(Model model) {
        model.addAttribute("curvePoint", new CurvePoint());
        return "curvePoint/add";
    }

    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute CurvePoint curvePoint,
                       BindingResult result,
                       Model model,
                       RedirectAttributes ra,
                       Principal principal) {

    logger.info("User {} is attempting to create CurvePoint: curveId={}, term={}", 
            principal.getName(), curvePoint.getCurveId(), curvePoint.getTerm());

    if (result.hasErrors()) {
        logger.warn("Validation errors: {}", result.getAllErrors());
        return "curvePoint/add";
    }

    try {
        curvePoint.setCreationName(principal.getName());

        CurvePoint saved = curvePointService.create(curvePoint);
        
        logger.info("CurvePoint created: ID={} by User={}", saved.getId(), principal.getName());
        ra.addFlashAttribute("successMessage", "Curve Point created successfully");
        return "redirect:/curvePoint/list";

        } catch (ConstraintViolationException e) {
        logger.error("Constraint violation", e);
        model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
        return "curvePoint/add";
        } catch (IllegalArgumentException e) {
        logger.error("Error creating CurvePoint", e);
        model.addAttribute("errorMessage", e.getMessage());
        return "curvePoint/add";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        logger.info("Loading CurvePoint for update: ID={}", id);

        try {
            CurvePoint curvePoint = curvePointService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("CurvePoint not found"));
            model.addAttribute("curvePoint", curvePoint);
            return "curvePoint/update";
        } catch (IllegalArgumentException e) {
            logger.warn("CurvePoint not found: ID={}", id);
            ra.addFlashAttribute("errorMessage", "Curve Point not found");
            return "redirect:/curvePoint/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCurvePoint(@PathVariable Integer id,
                                @Valid @ModelAttribute CurvePoint curvePoint,
                                BindingResult result,
                                Model model,
                                RedirectAttributes ra) {
        logger.info("Updating CurvePoint: ID={}", id);

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            curvePoint.setId(id);
            return "curvePoint/update";
        }

        try {
            CurvePoint existing = curvePointService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CurvePoint not found"));
            curvePoint.setCreationName(existing.getCreationName());

            CurvePoint updated = curvePointService.update(id, curvePoint);
            logger.info("CurvePoint updated: ID={}", updated.getId());
            ra.addFlashAttribute("successMessage", "Curve Point updated successfully");
            return "redirect:/curvePoint/list";
        } catch (ConstraintViolationException e) {
            logger.error("Constraint violation", e);
            curvePoint.setId(id);
            model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "curvePoint/update";
        } catch (IllegalArgumentException e) {
            logger.error("Error updating CurvePoint", e);
            curvePoint.setId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "curvePoint/update";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCurvePoint(@PathVariable Integer id, 
                                RedirectAttributes ra,
                                @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("User {} attempting to delete CurvePoint ID={}", userDetails.getUsername(), id);
        
        try {
            curvePointService.deleteById(id, userDetails);
            logger.info("CurvePoint deleted: ID={} by User={}", id, userDetails.getUsername());
            ra.addFlashAttribute("successMessage", "Curve Point deleted successfully");
        } catch (AccessDeniedException e) {
            logger.warn("Unauthorized delete attempt on CurvePoint {} by user {}", id, userDetails.getUsername());
            ra.addFlashAttribute("errorMessage", "Error: You are not authorized to delete this item.");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting CurvePoint", e);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            ra.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }
        
        return "redirect:/curvePoint/list";
    }
}