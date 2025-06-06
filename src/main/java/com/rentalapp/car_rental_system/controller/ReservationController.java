package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Reservation createReservation(
            @RequestParam String username,
            @RequestParam Long carId,
            @RequestParam Set<Extra> extras,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return reservationService.createReservation(username, carId, extras, startTime, endTime);
    }

    @PostMapping("/rent")
    @PreAuthorize("hasRole('USER')")
    public String createReservationWeb(
            @RequestParam String username,
            @RequestParam Long carId,
            @RequestParam Set<Extra> extras,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam int duration,
            Model model
    ) {
        LocalTime endTime = startTime.plusHours(duration);
        try {
            reservationService.createReservation(username, carId, extras, startTime, endTime);
            model.addAttribute("message", "Reservation successful!");
            return "redirect:/cars";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "rent-car";
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Reservation updateReservation(
            @PathVariable Long id,
            @RequestParam Set<Extra> extras,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return reservationService.updateReservation(id, startTime, endTime, extras);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('USER')")
    public List<Reservation> getUserReservations(@PathVariable String username) {
        return reservationService.getReservationsByUsername(username);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public void cancelReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }
}
