package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Role;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock private UserService userService;
    @Mock private CarService carService;

    @InjectMocks private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowAdminUserPage() {
        List<User> users = List.of(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);

        Model model = new ConcurrentModel();
        String view = adminController.showAdminUserPage(model);

        assertThat(view).isEqualTo("admin-users");
        assertThat(model.getAttribute("users")).isEqualTo(users);
    }

    @Test
    void testCreateAdmin() {
        String result = adminController.createAdmin(
                "John", "Doe", "admin", "admin@example.com", "pass"
        );

        verify(userService).createUser("John", "Doe", "admin", "admin@example.com", "pass", Role.ADMIN);
        assertThat(result).isEqualTo("redirect:/cars");
    }

    @Test
    void testPromoteToAdmin() {
        String result = adminController.promoteToAdmin("user1");

        verify(userService).promoteUserToAdmin("user1");
        assertThat(result).isEqualTo("redirect:/admin/admin-users");
    }

    @Test
    void testRevokeAdminRights() {
        String result = adminController.revokeAdminRights("admin1");

        verify(userService).revokeAdminRights("admin1");
        assertThat(result).isEqualTo("redirect:/admin/admin-users");
    }

    @Test
    void testDeleteUser() {
        String result = adminController.deleteUser("user1");

        verify(userService).deleteUser("user1");
        assertThat(result).isEqualTo("redirect:/admin/admin-users");
    }

    @Test
    void testShowAddCarForm() {
        Model model = new ConcurrentModel();
        String view = adminController.showAddCarForm(model);

        assertThat(view).isEqualTo("add-car");
        assertThat(model.getAttribute("car")).isInstanceOf(Car.class);
    }

    @Test
    void testAddCar() {
        Car car = new Car();
        String result = adminController.addCar(car);

        verify(carService).addCar(car);
        assertThat(result).isEqualTo("redirect:/cars");
    }
}
