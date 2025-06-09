package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.repository.ReservationRepository;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private static final Logger log = LoggerFactory.getLogger(CarController.class);

    private final CarService carService;
    private final ReservationService reservationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseBody
    public Car addCar(@RequestBody Car car) {
        log.info("Adding car: {} {}", car.getBrand(), car.getModel());
        return carService.addCar(car);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteCar(@PathVariable Long id) {
        log.info("Deleting car with ID: {}", id);
        carService.deleteCar(id);
        return "redirect:/cars";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Car updateCar(@PathVariable Long id, @RequestBody Car updatedCar) {
        log.info("Updating car with ID: {}", id);
        return carService.updateCar(id, updatedCar);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Car getCarById(@PathVariable Long id) {
        log.debug("Getting car by ID: {}", id);
        return carService.getCarById(id);
    }

    @GetMapping
    public String showAllCars(Model model) {
        log.info("Showing car list");
        model.addAttribute("cars", carService.getAllCars());
        return "cars";
    }

    @GetMapping("/details/{slug}")
    public String showCarRentalPage(@PathVariable String slug, Model model) {
        log.info("Opening rent page for car: {}", slug);
        Car car = carService.getCarBySlug(slug);
        List<Reservation> reservations = reservationService.getReservationsByCarId(car.getId());
        model.addAttribute("car", car);
        model.addAttribute("reservations", reservations);
        return "rent-car";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{carId}/rent")
    public String rentCar(@PathVariable Long carId,
                          @RequestParam String username,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                          Model model) {
        log.info("User '{}' attempting to rent car ID {} on {}", username, carId, date);
        try {
            reservationService.createReservation(username, carId, Collections.emptySet(), date, startTime, endTime);
            model.addAttribute("message", "Reservation successful!");
            log.info("Reservation was successful for car ID {} by user {}", carId, username);
            return "reservation-confirmation";
        } catch (IllegalArgumentException e) {
            log.warn("Reservation was failed for user {}: {}", username, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "rent-car";
        }
    }
}
