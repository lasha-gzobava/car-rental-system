package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.service.CarService;
import com.rentalapp.car_rental_system.service.ReservationService;
import com.rentalapp.car_rental_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock private CarService carService;
    @Mock private ReservationService reservationService;
    @Mock private UserService userService;

    @InjectMocks private PaymentController paymentController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowPaymentPage() {
        Long carId = 1L;
        int duration = 2;
        LocalTime startTime = LocalTime.of(10, 0);
        LocalDate date = LocalDate.of(2025, 6, 10);

        Car car = new Car();
        car.setPricePerHour(50.0);
        car.setId(carId);

        when(carService.getCarById(carId)).thenReturn(car);

        Model model = new ConcurrentModel();

        String view = paymentController.showPaymentPage(
                carId, "user1", date, startTime, duration, Set.of(Extra.GPS, Extra.INSURANCE), model
        );

        assertThat(view).isEqualTo("payment");
        double expectedTotal = car.getPricePerHour() * duration + Extra.GPS.getPrice() + Extra.INSURANCE.getPrice();
        assertThat(model.getAttribute("total")).isEqualTo(expectedTotal);
    }

    @Test
    void testProcessPayment_Success() {
        Long carId = 1L;
        int hours = 2;
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDate date = LocalDate.of(2025, 6, 10);
        LocalTime endTime = startTime.plusHours(hours);

        Car car = new Car();
        car.setSlug("tesla-model-s");
        car.setId(carId);

        User user = new User();
        user.setUsername("user1");

        when(carService.getCarById(carId)).thenReturn(car);
        when(carService.isCarAvailable(carId, date, startTime, endTime)).thenReturn(true);

        Model model = new ConcurrentModel();

        String result = paymentController.processPayment(
                carId, hours, date, startTime,
                "1234", "John Doe", "12/26", "123",
                user, model
        );

        verify(reservationService).createReservation("user1", carId, Set.of(), date, startTime, endTime);
        assertThat(result).isEqualTo("redirect:/cars/details/tesla-model-s");
    }

    @Test
    void testProcessPayment_UnavailableCar() {
        Long carId = 1L;
        int hours = 2;
        LocalDate date = LocalDate.of(2025, 6, 10);
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = startTime.plusHours(hours);

        Car car = new Car();
        car.setId(carId);

        User user = new User();
        user.setUsername("user1");

        when(carService.getCarById(carId)).thenReturn(car);
        when(carService.isCarAvailable(carId, date, startTime, endTime)).thenReturn(false);
        when(reservationService.getReservationsByCarId(carId)).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String view = paymentController.processPayment(
                carId, hours, date, startTime,
                "1234", "John Doe", "12/26", "123",
                user, model
        );

        assertThat(view).isEqualTo("rent-car");
        assertThat(model.getAttribute("error")).isEqualTo("This car is already booked for the selected time.");
        assertThat(model.getAttribute("car")).isEqualTo(car);
    }

    @Test
    void testProcessPayment_NoUser() {
        Model model = new ConcurrentModel();
        String result = paymentController.processPayment(
                1L, 2, LocalDate.of(2025, 6, 10), LocalTime.of(10, 0),
                "1234", "John Doe", "12/26", "123",
                null, model
        );
        assertThat(result).isEqualTo("redirect:/login");
    }
}
