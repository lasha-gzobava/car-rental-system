package com.rentalapp.car_rental_system.repository;

import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByCarId(Long carId);
}
