package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.IBidListService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String home(Model model) {
        List<BidList> bidLists = bidListService.findAll();
        model.addAttribute("bidLists", bidLists);
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
                          RedirectAttributes ra) {
        logger.info("Creating BidList: account={}, type={}", bidList.getAccount(), bidList.getType());

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            return "bidList/add";
        }

        try {
            BidList saved = bidListService.create(bidList);
            logger.info("BidList created: ID={}", saved.getBidListId());
            ra.addFlashAttribute("successMessage", "BidList created successfully");
            return "redirect:/bidList/list";
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
            BidList updated = bidListService.update(id, bidList);
            logger.info("BidList updated: ID={}", updated.getBidListId());
            ra.addFlashAttribute("successMessage", "BidList updated successfully");
            return "redirect:/bidList/list";
        } catch (IllegalArgumentException e) {
            logger.error("Error updating BidList", e);
            bidList.setBidListId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "bidList/update";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBid(@PathVariable Integer id, RedirectAttributes ra) {
        logger.info("Deleting BidList: ID={}", id);

        try {
            bidListService.deleteById(id);
            logger.info("BidList deleted: ID={}", id);
            ra.addFlashAttribute("successMessage", "BidList deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting BidList", e);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bidList/list";
    }
}