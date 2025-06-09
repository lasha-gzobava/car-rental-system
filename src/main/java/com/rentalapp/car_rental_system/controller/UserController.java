package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password) {
        log.info("Register user with username {}", username);
        userService.createUser(firstName, lastName, username, email, password, Role.USER);
        return "redirect:/login";
    }

    @PutMapping("/{username}")
    public User update(@PathVariable String username,
                       @RequestParam String firstName,
                       @RequestParam String lastName,
                       @RequestParam String email,
                       @RequestParam String password){
        log.info("Updating user '{}'", username);
        return userService.updateUser(username, firstName, lastName, email, password);
    }

    @GetMapping("/{username}")
    public User get(@PathVariable String username){
        log.debug("Getting user by username '{}'", username);
        return userService.getUserByUsername(username);
    }

    @DeleteMapping("/{username}")
    public void delete(@PathVariable String username){
        log.info("Deleting user '{}'", username);
        userService.deleteUser(username);
    }
}
