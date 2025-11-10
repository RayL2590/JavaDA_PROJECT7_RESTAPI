package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.IRuleNameService;
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
@RequestMapping("/ruleName")
public class RuleNameController {

    private static final Logger logger = LoggerFactory.getLogger(RuleNameController.class);
    private final IRuleNameService ruleNameService;

    public RuleNameController(IRuleNameService ruleNameService) {
        this.ruleNameService = ruleNameService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        List<RuleName> ruleNames = ruleNameService.findAll();
        model.addAttribute("ruleNames", ruleNames);
        return "ruleName/list";
    }

    @GetMapping("/add")
    public String addRuleForm(Model model) {
        model.addAttribute("ruleName", new RuleName());
        return "ruleName/add";
    }

    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute RuleName ruleName,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra) {
        logger.info("Creating RuleName: name={}", ruleName.getName());

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            return "ruleName/add";
        }

        try {
            RuleName saved = ruleNameService.create(ruleName);
            logger.info("RuleName created: ID={}", saved.getId());
            ra.addFlashAttribute("successMessage", "Rule created successfully");
            return "redirect:/ruleName/list";
        } catch (IllegalArgumentException e) {
            logger.error("Error creating RuleName", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "ruleName/add";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        logger.info("Loading RuleName for update: ID={}", id);

        try {
            RuleName ruleName = ruleNameService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("RuleName not found"));
            model.addAttribute("ruleName", ruleName);
            return "ruleName/update";
        } catch (IllegalArgumentException e) {
            logger.warn("RuleName not found: ID={}", id);
            ra.addFlashAttribute("errorMessage", "Rule not found");
            return "redirect:/ruleName/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateRuleName(@PathVariable Integer id,
                                 @Valid @ModelAttribute RuleName ruleName,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes ra) {
        logger.info("Updating RuleName: ID={}", id);

        if (result.hasErrors()) {
            logger.warn("Validation errors: {}", result.getAllErrors());
            ruleName.setId(id);
            return "ruleName/update";
        }

        try {
            RuleName updated = ruleNameService.update(id, ruleName);
            logger.info("RuleName updated: ID={}", updated.getId());
            ra.addFlashAttribute("successMessage", "Rule updated successfully");
            return "redirect:/ruleName/list";
        } catch (IllegalArgumentException e) {
            logger.error("Error updating RuleName", e);
            ruleName.setId(id);
            model.addAttribute("errorMessage", e.getMessage());
            return "ruleName/update";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteRuleName(@PathVariable Integer id, RedirectAttributes ra) {
        logger.info("Deleting RuleName: ID={}", id);

        try {
            ruleNameService.deleteById(id);
            logger.info("RuleName deleted: ID={}", id);
            ra.addFlashAttribute("successMessage", "Rule deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting RuleName", e);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ruleName/list";
    }
}