package com.rentalapp.car_rental_system.service;

import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser_successful() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser("John", "Doe", "user", "user@example.com", "pass123", Role.USER);

        assertEquals("user", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals(Role.USER, result.getRole());
    }


    @Test
    public void createUser_emailExists_throws() {
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(new User()));
        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("John", "Doe", "user", "email", "pass", Role.USER));
    }

    @Test
    public void updateUser_successful() {
        User existing = new User();
        existing.setUsername("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser("user", "New", "Name", "new@email", "newpass");

        assertEquals("New", updated.getFirstName());
        assertEquals("encoded", updated.getPassword());
    }

    @Test
    public void getUserByUsername_found() {
        User user = new User();
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("john");

        assertEquals("john", result.getUsername());
    }

    @Test
    public void deleteUser_successful() {
        User user = new User();
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        userService.deleteUser("john");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void updatePayment_addsCorrectly() {
        User user = new User();
        user.setUsername("john");
        user.setPayment(100);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        userService.updatePayment("john", 50);

        assertEquals(150, user.getPayment());
    }

    @Test
    public void promoteUserToAdmin_successful() {
        User user = new User();
        user.setUsername("john");
        user.setRole(Role.USER);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User promoted = userService.promoteUserToAdmin("john");

        assertEquals(Role.ADMIN, promoted.getRole());
    }

    @Test
    public void revokeAdminRights_successful() {
        User user = new User();
        user.setRole(Role.ADMIN);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        userService.revokeAdminRights("john");

        assertEquals(Role.USER, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    public void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
        assertEquals(2, userService.getAllUsers().size());
    }
}
