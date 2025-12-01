package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.ITradeService;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/trade")
public class TradeController {

    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);
    private final ITradeService tradeService;

    public TradeController(ITradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        List<Trade> trades = tradeService.findAll();
        model.addAttribute("trades", trades);
        return "trade/list";
    }

    @GetMapping("/add")
    public String addTradeForm(Model model) {
        model.addAttribute("trade", new Trade());
        return "trade/add";
    }

    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute Trade trade,
                        BindingResult result,
                        Model model,
                        RedirectAttributes ra) {
        logger.info("Creating Trade: account={}, type={}", trade.getAccount(), trade.getType());

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            return "trade/add";
        }

        try {
            Trade saved = tradeService.create(trade);
            logger.info("Trade created: ID={}", saved.getTradeId());
            ra.addFlashAttribute("successMessage", "Trade created successfully");
            return "redirect:/trade/list";
        } catch (ConstraintViolationException e) {
            logger.error("Constraint violation", e);
            model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "trade/add";
        } catch (IllegalArgumentException e) {
            logger.error("Error creating Trade", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "trade/add";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        logger.info("Loading Trade for update: ID={}", id);

        try {
            Trade trade = tradeService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Trade not found"));
            model.addAttribute("trade", trade);
            return "trade/update";
        } catch (IllegalArgumentException e) {
            logger.warn("Trade not found: ID={}", id);
            ra.addFlashAttribute("errorMessage", "Trade not found");
            return "redirect:/trade/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateTrade(@PathVariable Integer id,
                            @Valid @ModelAttribute Trade trade,
                            BindingResult result,
                            Model model,
                            RedirectAttributes ra) {
        logger.info("Updating Trade: ID={}", id);

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            trade.setTradeId(id);
            return "trade/update";
        }

        try {
            Trade updated = tradeService.update(id, trade);
            logger.info("Trade updated: ID={}", updated.getTradeId());
            ra.addFlashAttribute("successMessage", "Trade updated successfully");
            return "redirect:/trade/list";
        } catch (ConstraintViolationException e) {
            logger.error("Constraint violation", e);
            trade.setTradeId(id);
            model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "trade/update";
        } catch (Exception e) {
            logger.error("Error updating Trade", e);
            trade.setTradeId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "trade/update";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTrade(@PathVariable Integer id, RedirectAttributes ra) {
        logger.info("Deleting Trade: ID={}", id);

        try {
            tradeService.deleteById(id);
            logger.info("Trade deleted: ID={}", id);
            ra.addFlashAttribute("successMessage", "Trade deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting Trade", e);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/trade/list";
    }
}