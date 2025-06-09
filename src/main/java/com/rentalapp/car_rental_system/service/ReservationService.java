package com.rentalapp.car_rental_system.service;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.repository.CarRepository;
import com.rentalapp.car_rental_system.repository.ReservationRepository;
import com.rentalapp.car_rental_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;


    public Reservation createReservation(String username, Long carId, Set<Extra> extras,
                                         LocalDate date,
                                         LocalTime startTime, LocalTime endTime) {

        log.info("Creating reservation for user '{}' and car ID {} on {}", username, carId, date);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> {
                    log.warn("Car not found with ID {}", carId);
                    return new IllegalArgumentException("Car not found");
                });

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username '{}'", username);
                    return new IllegalArgumentException("User not found");
                });

        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        if (hours <= 0) {
            log.warn("Invalid reservation duration: start={} end={}", startTime, endTime);
            throw new IllegalArgumentException("End time must be after start time.");
        }


        List<Reservation> existingReservations = reservationRepository.findByCarId(carId).stream()
                .filter(r -> r.getDate().equals(date))
                .toList();

        for (Reservation r : existingReservations) {
            if (!(endTime.isBefore(r.getStartTime()) || startTime.isAfter(r.getEndTime()))) {
                log.warn("Reservation conflict: car ID {} already booked from {} to {}", carId, r.getStartTime(), r.getEndTime());
                throw new IllegalArgumentException("Car is already reserved during the selected time.");
            }
        }

        double extraPrice = extras.stream().mapToDouble(Extra::getPrice).sum();
        double price = extraPrice + (hours * car.getPricePerHour());

        Reservation reservation = new Reservation();
        reservation.setCar(car);
        reservation.setUser(user);
        reservation.setDate(date);
        reservation.setExtras(extras);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setTotalPrice(price);


        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation created: ID {} for user '{}' and car ID {}", saved.getId(), username, carId);
        return saved;
    }




    public Reservation updateReservation(Long reservationId,
                                         LocalTime newStartTime, LocalTime newEndTime,
                                         Set<Extra> newExtras) {

        log.info("Updating reservation ID {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation not found: ID {}", reservationId);
                    return new IllegalArgumentException("Reservation not found");
                });

        Car car = reservation.getCar();

        long hours = ChronoUnit.HOURS.between(newStartTime, newEndTime);
        if (hours <= 0) {
            log.warn("Invalid update: endTime {} is not after startTime {}", newEndTime, newStartTime);
            throw new IllegalArgumentException("Invalid reservation duration");
        }

        double extrasTotal = newExtras != null ? newExtras.stream().mapToDouble(Extra::getPrice).sum() : 0;
        double total = (hours * car.getPricePerHour()) + extrasTotal;


        reservation.setStartTime(newStartTime);
        reservation.setEndTime(newEndTime);
        reservation.setExtras(newExtras);
        reservation.setTotalPrice(total);

        Reservation updated = reservationRepository.save(reservation);
        log.info("Reservation updated: ID {}", reservationId);
        return updated;
    }

    public void deleteReservation(Long reservationId) {
        log.info("Deleting reservation ID {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation not found: ID {}", reservationId);
                    return new IllegalArgumentException("Reservation not found");
                });

        Car car = reservation.getCar();
        carRepository.save(car);
        reservationRepository.delete(reservation);
        log.info("Reservation deleted: ID {}", reservationId);
    }

    public Reservation getReservation(Long reservationId) {
        log.debug("Fetching reservation ID {}", reservationId);
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation not found: ID {}", reservationId);
                    return new IllegalArgumentException("Reservation not found");
                });
    }


    public List<Reservation> getReservationsByUsername(String username) {
        log.debug("Fetching reservations for user '{}'", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found for reservations: '{}'", username);
                    return new IllegalArgumentException("User not found");
                });
        return reservationRepository.findByUser(user);
    }


    public List<Reservation> getAllReservations() {
        log.debug("Fetching all reservations");
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByCarId(Long id) {
        log.debug("Fetching reservations for car ID {}", id);
        return reservationRepository.findByCarId(id);
    }
}
