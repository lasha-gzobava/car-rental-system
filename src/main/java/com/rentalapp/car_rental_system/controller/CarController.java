package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.repository.ReservationRepository;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final ReservationService reservationService;



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseBody
    public Car addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Car updateCar(@PathVariable Long id, @RequestBody Car updatedCar) {
        return carService.updateCar(id, updatedCar);
    }



    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Car getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }


    @GetMapping
    public String showAllCars(Model model) {
        model.addAttribute("cars", carService.getAllCars());
        return "cars";
    }

    @GetMapping("/details/{slug}")
    public String showCarRentalPage(@PathVariable String slug, Model model) {
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
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                          Model model) {
        try {
            reservationService.createReservation(username, carId, Collections.emptySet(), startTime, endTime);
            model.addAttribute("message", "Reservation successful!");
            return "reservation-confirmation";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "rent-car";
        }
    }




}