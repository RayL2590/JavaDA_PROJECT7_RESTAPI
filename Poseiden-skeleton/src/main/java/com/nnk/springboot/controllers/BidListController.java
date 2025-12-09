package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.IBidListService;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/bidList")
public class BidListController {

    private static final Logger logger = LoggerFactory.getLogger(BidListController.class);
    private final IBidListService bidListService;

    public BidListController(IBidListService bidListService) {
        this.bidListService = bidListService;
    }

    @GetMapping("/list")
    public String home(Model model, Authentication authentication) {
        List<BidList> bidLists = bidListService.findAll();
        model.addAttribute("bidLists", bidLists);
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute("currentRoles", authentication.getAuthorities());
        return "bidList/list";
    }

    @GetMapping("/add")
    public String addBidForm(Model model) {
        model.addAttribute("bidList", new BidList());
        return "bidList/add";
    }

    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute BidList bidList,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra, Principal principal) {
        logger.info("User {} is attempting to create BidList: account={}, type={}", 
            principal.getName(), bidList.getAccount(), bidList.getType());

    if (result.hasErrors()) {
        logger.warn("Validation errors: {}", result.getAllErrors());
        return "bidList/add";
    }

    try {
        bidList.setCreationName(principal.getName());

        BidList saved = bidListService.create(bidList);
        
        logger.info("BidList created: ID={} by User={}", saved.getBidListId(), principal.getName());
        ra.addFlashAttribute("successMessage", "BidList created successfully");
        return "redirect:/bidList/list";

    } catch (ConstraintViolationException e) {
        logger.error("Constraint violation", e);
        model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
        return "bidList/add";
    } catch (IllegalArgumentException e) {
        logger.error("Error creating BidList", e);
        model.addAttribute("errorMessage", e.getMessage());
        return "bidList/add";
    }
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        logger.info("Loading BidList for update: ID={}", id);

        try {
            BidList bidList = bidListService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("BidList not found"));
            model.addAttribute("bidList", bidList);
            return "bidList/update";
        } catch (IllegalArgumentException e) {
            logger.warn("BidList not found: ID={}", id);
            ra.addFlashAttribute("errorMessage", "BidList not found");
            return "redirect:/bidList/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateBid(@PathVariable Integer id,
                        @Valid @ModelAttribute BidList bidList,
                        BindingResult result,
                        Model model,
                        RedirectAttributes ra) {
        logger.info("Updating BidList: ID={}", id);

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            bidList.setBidListId(id);
            return "bidList/update";
        }

        try {
            BidList existing = bidListService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BidList not found"));
            bidList.setCreationName(existing.getCreationName());

            BidList updated = bidListService.update(id, bidList);
            logger.info("BidList updated: ID={}", updated.getBidListId());
            ra.addFlashAttribute("successMessage", "BidList updated successfully");
            return "redirect:/bidList/list";
        } catch (ConstraintViolationException e) {
            logger.error("Constraint violation", e);
            bidList.setBidListId(id);
            model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "bidList/update";
        } catch (IllegalArgumentException e) {
            logger.error("Error updating BidList", e);
            bidList.setBidListId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "bidList/update";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBid(@PathVariable Integer id, 
                        Model model, 
                        RedirectAttributes ra,
                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            bidListService.deleteById(id, userDetails);
            logger.info("BidList deleted: ID={} by User={}", id, userDetails.getUsername());
            ra.addFlashAttribute("successMessage", "BidList deleted successfully");
        } catch (AccessDeniedException e) {
            logger.warn("Unauthorized delete attempt by user {}: {}", userDetails.getUsername(), e.getMessage());
            ra.addFlashAttribute("errorMessage", "Error: You are not authorized to delete this item.");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting BidList", e);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bidList/list";
    }
}