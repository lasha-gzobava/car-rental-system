package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CarService carService;


    @GetMapping("/admin-users")
    public String showAdminUserPage(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-users";
    }


    @PostMapping("/create")
    public String createAdmin(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String password) {
        userService.createUser(firstName, lastName, username, email, password, Role.ADMIN);
        return "redirect:/cars";
    }


    @PostMapping("/promote")
    public String promoteToAdmin(@RequestParam String username) {
        userService.promoteUserToAdmin(username);
        return "redirect:/admin/admin-users";
    }

    @PostMapping("/revoke")
    public String revokeAdminRights(@RequestParam String username) {
        userService.revokeAdminRights(username);
        return "redirect:/admin/admin-users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String username) {
        userService.deleteUser(username);
        return "redirect:/admin/admin-users";
    }



    @GetMapping("/add-car")
    public String showAddCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "add-car";
    }


    @PostMapping("/add-car")
    public String addCar(@ModelAttribute Car car) {
        carService.addCar(car);
        return "redirect:/cars";
    }
}