package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.ReservationService;
import com.rentalapp.car_rental_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final CarService carService;
    private final ReservationService reservationService;
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public String processPayment(@RequestParam Long carId,
                                 @RequestParam int hours,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                                 @RequestParam String cardNumber,
                                 @RequestParam String cardHolder,
                                 @RequestParam String expiryDate,
                                 @RequestParam String cvv,
                                 @AuthenticationPrincipal com.rentalapp.car_rental_system.entity.User user,
                                 Model model) {

        if (user == null) {
            log.warn("User is not authenticated");
            return "redirect:/login";
        }

        String username = user.getUsername();
        log.info("User '{}' Starting payment for car ID {} on {}", username, carId, date);

        Car car = carService.getCarById(carId);
        LocalTime endTime = startTime.plusHours(hours);

        if (!carService.isCarAvailable(carId, date, startTime, endTime)) {
            log.warn("Car ID {} is not available for the selected time", carId);
            model.addAttribute("car", car);
            model.addAttribute("reservations", reservationService.getReservationsByCarId(carId));
            model.addAttribute("error", "This car is already booked for the selected time.");
            return "rent-car";
        }

        reservationService.createReservation(username, carId, Set.of(), date, startTime, endTime);
        log.info("Payment successful, reservation created for car ID {} by user '{}'", carId, username);

        return "redirect:/cars/details/" + car.getSlug();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{carId}")
    public String showPaymentPage(@PathVariable Long carId,
                                  @RequestParam String username,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                                  @RequestParam int duration,
                                  @RequestParam(required = false) Set<Extra> extras,
                                  Model model) {

        log.info("User '{}' accessed payment page for car ID {} on {}", username, carId, date);

        LocalTime endTime = startTime.plusHours(duration);
        Car car = carService.getCarById(carId);

        double extraPrice = (extras != null) ? extras.stream().mapToDouble(Extra::getPrice).sum() : 0;
        double total = extraPrice + car.getPricePerHour() * duration;

        model.addAttribute("car", car);
        model.addAttribute("username", username);
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("hours", duration);
        model.addAttribute("extras", extras);
        model.addAttribute("total", total);

        return "payment";
    }
}
