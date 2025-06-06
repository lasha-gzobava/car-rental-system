package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password) {
        System.out.println("Register endpoint was called");
        userService.createUser(firstName, lastName, username, email, password, Role.USER); // Always USER
        return "redirect:/login";
    }



    @PutMapping("/{username}")
    public User update(@PathVariable String username,
                       @RequestParam String firstName,
                       @RequestParam String lastName,
                       @RequestParam String email,
                       @RequestParam String password){
        return userService.updateUser(username, firstName, lastName, email, password);
    }

    @GetMapping("/{username}")
    public User get(@PathVariable String username){
        return userService.getUserByUsername(username);
    }

    @DeleteMapping("/{username}")
    public void delete(@PathVariable String username){
        userService.deleteUser(username);
    }


}
