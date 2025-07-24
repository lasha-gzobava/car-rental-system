package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        String view = userController.register(
                "John", "Doe", "johndoe", "john@example.com", "pass123"
        );

        verify(userService).createUser("John", "Doe", "johndoe", "john@example.com", "pass123", Role.USER);
        assertThat(view).isEqualTo("redirect:/login");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        when(userService.updateUser("johndoe", "John", "Doe", "john@example.com", "newpass")).thenReturn(user);

        User result = userController.update("johndoe", "John", "Doe", "john@example.com", "newpass");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void testGetUser() {
        User user = new User();
        when(userService.getUserByUsername("johndoe")).thenReturn(user);

        User result = userController.get("johndoe");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void testDeleteUser() {
        userController.delete("johndoe");

        verify(userService).deleteUser("johndoe");
    }
}
