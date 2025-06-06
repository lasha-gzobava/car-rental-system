package com.rentalapp.car_rental_system.repository;

import com.rentalapp.car_rental_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // ✅ use this
    Optional<User> findByUsername(String username);  // ✅ Add this method
    void deleteByEmail(String email);
}
