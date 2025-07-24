package com.rentalapp.car_rental_system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {


    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String firstName, String lastName, String username, String email, String password, Role role) {
        log.info("Saving user: {}", username);


        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            log.warn("Invalid email format for: {}", email);
            throw new IllegalArgumentException("Invalid email format");
        }


        if (password.length() < 6) {
            log.warn("Password too short for user: {}", username);
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Email already in use for user: {}", username);
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setPayment(0);

        System.out.println("Registering user: " + username);

        User saved = userRepository.save(user);
        log.info("User registered: {} (ID: {})", username, saved.getId());
        return saved;
    }





    public User updateUser(String username, String firstName, String lastName, String email, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User updated = userRepository.save(user);
        log.info("User updated: {}", username);
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });
    }

    public void deleteUser(String username) {
        log.info("Deleting user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });
        userRepository.delete(user);
        log.info("User deleted: {}", username);
    }

    @Transactional
    public void updatePayment(String username, int amount) {
        log.info("Updating payment for user '{}' by {} EUR", username, amount);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });
        user.setPayment(user.getPayment() + amount);
        log.info("Updated payment for user '{}' by {} EUR", username, amount);
    }


    public User promoteUserToAdmin(String username) {
        log.info("Promoting user to admin: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });
        user.setRole(Role.ADMIN);
        return userRepository.save(user);
    }

    public void revokeAdminRights(String username) {
        log.info("Revoking admin rights for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.USER);
            userRepository.save(user);
            log.info("User {} demoted to USER", username);
        } else {
            log.info("User {} is not an admin, no action taken", username);
        }
    }




    public List<User> getAllUsers() {
        log.debug("Getting all users");
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        log.debug("Looking up user by username: {}", username);
        return userRepository.findByUsername(username);
    }


    public Optional<User> findByEmail(String email) {
        log.debug("Looking up user by email: {}", email);
        return userRepository.findByEmail(email);
    }
}
