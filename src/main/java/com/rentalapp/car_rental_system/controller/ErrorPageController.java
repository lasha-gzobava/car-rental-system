package com.rentalapp.car_rental_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/error-db")
    public String showDatabaseError(Model model) {
        model.addAttribute("message", "We are experiencing database issues. Please try again later.");
        return "error-db"; 
    }
}
