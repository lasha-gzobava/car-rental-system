package com.rentalapp.car_rental_system.repository;

import com.rentalapp.car_rental_system.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findBySlug(String slug);  // Query by slug

}
