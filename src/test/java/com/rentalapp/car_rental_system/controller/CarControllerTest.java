package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CarControllerTest {

    @Mock private CarService carService;
    @Mock private ReservationService reservationService;

    @InjectMocks private CarController carController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCar() {
        Car car = new Car();
        when(carService.addCar(car)).thenReturn(car);

        Car result = carController.addCar(car);

        assertThat(result).isEqualTo(car);
        verify(carService).addCar(car);
    }

    @Test
    void testDeleteCar() {
        carController.deleteCar(1L);
        verify(carService).deleteCar(1L);
    }

    @Test
    void testUpdateCar() {
        Car updated = new Car();
        when(carService.updateCar(1L, updated)).thenReturn(updated);

        Car result = carController.updateCar(1L, updated);

        assertThat(result).isEqualTo(updated);
    }

    @Test
    void testGetCarById() {
        Car car = new Car();
        when(carService.getCarById(1L)).thenReturn(car);

        Car result = carController.getCarById(1L);

        assertThat(result).isEqualTo(car);
    }

    @Test
    void testShowAllCars() {
        List<Car> cars = List.of(new Car(), new Car());
        when(carService.getAllCars()).thenReturn(cars);

        Model model = new ConcurrentModel();
        String view = carController.showAllCars(model);

        assertThat(view).isEqualTo("cars");
        assertThat(model.getAttribute("cars")).isEqualTo(cars);
    }

    @Test
    void testShowCarRentalPage() {
        Car car = new Car();
        car.setId(1L);
        List<Reservation> reservations = List.of(new Reservation());

        when(carService.getCarBySlug("tesla")).thenReturn(car);
        when(reservationService.getReservationsByCarId(1L)).thenReturn(reservations);

        Model model = new ConcurrentModel();
        String view = carController.showCarRentalPage("tesla", model);

        assertThat(view).isEqualTo("rent-car");
        assertThat(model.getAttribute("car")).isEqualTo(car);
        assertThat(model.getAttribute("reservations")).isEqualTo(reservations);
    }

    @Test
    void testRentCar_Success() {
        Model model = new ConcurrentModel();

        String view = carController.rentCar(
                1L, "user1", LocalTime.of(10, 0), LocalTime.of(12, 0), model
        );

        verify(reservationService).createReservation(
                eq("user1"), eq(1L), anySet(), eq(LocalTime.of(10, 0)), eq(LocalTime.of(12, 0))
        );

        assertThat(view).isEqualTo("reservation-confirmation");
        assertThat(model.getAttribute("message")).isEqualTo("Reservation successful!");
    }

    @Test
    void testRentCar_Failure() {
        doThrow(new IllegalArgumentException("Unavailable")).when(reservationService)
                .createReservation(any(), any(), any(), any(), any());

        Model model = new ConcurrentModel();
        String view = carController.rentCar(
                1L, "user1", LocalTime.of(10, 0), LocalTime.of(12, 0), model
        );

        assertThat(view).isEqualTo("rent-car");
        assertThat(model.getAttribute("error")).isEqualTo("Unavailable");
    }
}
