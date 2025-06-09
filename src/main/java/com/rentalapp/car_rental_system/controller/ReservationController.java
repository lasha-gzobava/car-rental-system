package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Reservation createReservation(
            @RequestParam String username,
            @RequestParam Long carId,
            @RequestParam Set<Extra> extras,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        log.info("Reservation request: user={}, carId={}, date={}, start={}, end={}", username, carId, date, startTime, endTime);
        return reservationService.createReservation(username, carId, extras, date, startTime, endTime);
    }

    @PostMapping("/rent")
    @PreAuthorize("hasRole('USER')")
    public String createReservationWeb(
            @RequestParam String username,
            @RequestParam Long carId,
            @RequestParam Set<Extra> extras,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam int duration,
            Model model
    ) {
        log.info("Web reservation request: user={}, carId={}, date={}, start={}, duration={}", username, carId, date, startTime, duration);
        LocalTime endTime = startTime.plusHours(duration);
        try {
            reservationService.createReservation(username, carId, extras, date, startTime, endTime);
            model.addAttribute("message", "Reservation successful!");
            return "redirect:/cars";
        } catch (IllegalArgumentException e) {
            log.warn("Reservation failed for user '{}': {}", username, e.getMessage());
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

        log.info("Updating reservation ID {}: start={}, end={}, extras={}", id, startTime, endTime, extras);
        return reservationService.updateReservation(id, startTime, endTime, extras);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('USER')")
    public List<Reservation> getUserReservations(@PathVariable String username) {
        log.debug("Getting reservations for user '{}'", username);
        return reservationService.getReservationsByUsername(username);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public void cancelReservation(@PathVariable Long id) {
        log.info("Cancelling reservation ID {}", id);
        reservationService.deleteReservation(id);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {
        log.debug("Getting all reservations for admin");
        return reservationService.getAllReservations();
    }
}
