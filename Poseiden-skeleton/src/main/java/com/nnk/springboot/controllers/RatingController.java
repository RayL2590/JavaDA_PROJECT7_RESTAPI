package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.IRatingService;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/rating")
public class RatingController {

    private final IRatingService ratingService;

    public RatingController(IRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("ratings", ratingService.findAll());
        return "rating/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating/add";
    }

    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("rating") Rating rating,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "rating/add";
        }

        try {
            ratingService.create(rating);
            redirectAttributes.addFlashAttribute("successMessage", "Rating added successfully");
            return "redirect:/rating/list";
        } catch (ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "redirect:/rating/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/rating/add";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Rating> ratingOpt = ratingService.findById(id);
        if (ratingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating introuvable : " + id);
            return "redirect:/rating/list";
        }
        model.addAttribute("rating", ratingOpt.get());
        return "rating/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("rating") Rating rating,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            rating.setId(id);
            return "rating/update";
        }

        try {
            ratingService.update(id, rating);
            redirectAttributes.addFlashAttribute("successMessage", "Rating updated successfully");
            return "redirect:/rating/list";
        } catch (ConstraintViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "redirect:/rating/update/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/rating/update/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Rating deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating not found");
        }
        return "redirect:/rating/list";
    }
}