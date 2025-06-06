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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;


    public Reservation createReservation(String username, Long carId, Set<Extra> extras,
                                         LocalTime startTime, LocalTime endTime) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        if (hours <= 0) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        // Check for conflicting reservations
        List<Reservation> existingReservations = reservationRepository.findByCarId(carId);
        for (Reservation r : existingReservations) {
            if (!(endTime.isBefore(r.getStartTime()) || startTime.isAfter(r.getEndTime()))) {
                throw new IllegalArgumentException("Car is already reserved during the selected time.");
            }
        }

        double extraPrice = extras.stream()
                .mapToDouble(Extra::getPrice)
                .sum();
        double price = extraPrice + (hours * car.getPricePerHour());

        Reservation reservation = new Reservation();
        reservation.setCar(car);
        reservation.setUser(user);
        reservation.setExtras(extras);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setTotalPrice(price);

        return reservationRepository.save(reservation);
    }



    public Reservation updateReservation(Long reservationId,
                                         LocalTime newStartTime, LocalTime newEndTime,
                                         Set<Extra> newExtras) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        Car car = reservation.getCar();

        long hours = ChronoUnit.HOURS.between(newStartTime, newEndTime);
        if (hours <= 0) {
            throw new IllegalArgumentException("Invalid reservation duration");
        }

        double extrasTotal = newExtras != null ? newExtras.stream().mapToDouble(Extra::getPrice).sum() : 0;
        double total = (hours * car.getPricePerHour()) + extrasTotal;


        reservation.setStartTime(newStartTime);
        reservation.setEndTime(newEndTime);
        reservation.setExtras(newExtras);
        reservation.setTotalPrice(total);

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        Car car = reservation.getCar();
        carRepository.save(car);

        reservationRepository.delete(reservation);
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }


    public List<Reservation> getReservationsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return reservationRepository.findByUser(user);
    }


    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByCarId(Long id) {
        return reservationRepository.findByCarId(id);
    }
}
