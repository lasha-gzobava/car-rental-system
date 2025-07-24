package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final CarService carService;

    @GetMapping("/admin-users")
    public String showAdminUserPage(Model model) {
        log.info("Loading admin management page");
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
        log.info("Creating new admin: {}", username);
        userService.createUser(firstName, lastName, username, email, password, Role.ADMIN);
        return "redirect:/cars";
    }

    @PostMapping("/promote")
    public String promoteToAdmin(@RequestParam String username) {
        log.info("Promoting user '{}' to ADMIN", username);
        userService.promoteUserToAdmin(username);
        return "redirect:/admin/admin-users";
    }

    @PostMapping("/revoke")
    public String revokeAdminRights(@RequestParam String username) {
        log.info("Revoking ADMIN rights for user '{}'", username);
        userService.revokeAdminRights(username);
        return "redirect:/admin/admin-users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String username) {
        log.info("Deleting user '{}'", username);
        userService.deleteUser(username);
        return "redirect:/admin/admin-users";
    }

    @GetMapping("/add-car")
    public String showAddCarForm(Model model) {
        log.info("Going to add car page");
        model.addAttribute("car", new Car());
        return "add-car";
    }

    @PostMapping("/add-car")
    public String addCar(@ModelAttribute Car car) {
        log.info("Adding car: {} {}", car.getBrand(), car.getModel());
        carService.addCar(car);
        return "redirect:/cars";
    }
}
