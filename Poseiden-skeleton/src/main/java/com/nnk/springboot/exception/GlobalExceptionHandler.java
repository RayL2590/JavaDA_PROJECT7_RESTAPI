package com.nnk.springboot.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, RedirectAttributes redirectAttributes) {
        logger.error("IllegalArgumentException", e);
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/error";
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolation(ConstraintViolationException e, RedirectAttributes redirectAttributes) {
        logger.error("ConstraintViolationException", e);
        redirectAttributes.addFlashAttribute("errorMessage", "Validation error: " + e.getMessage());
        return "redirect:/error";
    }


    @ExceptionHandler(OptimisticLockingFailureException.class)
    public String handleOptimisticLocking(OptimisticLockingFailureException e, Model model) {
        logger.error("OptimisticLockingFailureException", e);
        model.addAttribute("errorMessage", "Data was modified by another user. Please retry.");
        return "error";
    }


    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unexpected exception", e);
        model.addAttribute("errorMessage", "An unexpected error occurred");
        return "error";
    }
}