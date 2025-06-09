package com.rentalapp.car_rental_system.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private static final Logger log = LoggerFactory.getLogger(PageController.class);

    @GetMapping("/login")
    public String loginPage() {
        log.info("Entered login page");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        log.info("Entered register page");
        return "register";
    }
}
