package com.nnk.springboot.controllers;

import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/user/list")
    public String home(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("/user/add")
    public String addUser(UserDTO userDTO) {
        return "user/add";
    }

    @PostMapping("/user/validate")
    public String validate(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/add";
        }
        try {
            userService.createUser(userDTO, userDTO.getPassword());
            return "redirect:/user/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/add";
        }
    }

    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        UserDTO userDTO = userService.findById(id);
        model.addAttribute("userDTO", userDTO);
        return "user/update";
    }

    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/update";
        }
        try {
            userService.updateUser(id, userDTO, userDTO.getPassword());
            return "redirect:/user/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/update";
        }
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model) {
        userService.deleteUser(id);
        return "redirect:/user/list";
    }
}