package com.rentalapp.car_rental_system.repository;

import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveAndFindUserByUsernameAndEmail() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .username("janedoe")
                .email("jane@example.com")
                .password("hashed")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        Optional<User> byUsername = userRepository.findByUsername("janedoe");
        Optional<User> byEmail = userRepository.findByEmail("jane@example.com");

        assertThat(byUsername).isPresent();
        assertThat(byEmail).isPresent();
        assertThat(byUsername.get().getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    public void shouldDeleteByEmail() {
        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("jsmith")
                .email("john@example.com")
                .password("secret")
                .role(Role.USER)
                .build();

        userRepository.save(user);
        userRepository.deleteByEmail("john@example.com");

        assertThat(userRepository.findByEmail("john@example.com")).isNotPresent();
    }
}
