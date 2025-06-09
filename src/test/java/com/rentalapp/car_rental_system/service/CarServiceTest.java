package com.rentalapp.car_rental_system.service;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.CarBrand;
import com.rentalapp.car_rental_system.enums.CarType;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.repository.CarRepository;
import com.rentalapp.car_rental_system.repository.ReservationRepository;
import com.rentalapp.car_rental_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    @Mock private CarRepository carRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CarService carService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCars_returnsList() {
        when(carRepository.findAll()).thenReturn(List.of(new Car(), new Car()));
        assertEquals(2, carService.getAllCars().size());
    }

    @Test
    void getCarById_found() {
        Car car = new Car();
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        assertEquals(car, carService.getCarById(1L));
    }

    @Test
    void addCar_setsSlugAndSaves() {
        Car car = new Car();
        car.setBrand(CarBrand.AUDI);
        car.setModel("Q7");
        when(carRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Car saved = carService.addCar(car);
        assertTrue(saved.getSlug().contains("audi-q7"));
    }

    @Test
    void updateCar_appliesChangesAndSaves() {
        Car oldCar = new Car();
        oldCar.setId(1L);
        oldCar.setModel("X");
        oldCar.setBrand(CarBrand.TESLA);
        oldCar.setType(CarType.SEDAN);
        oldCar.setPricePerHour(20);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oldCar));
        when(carRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Car updated = new Car();
        updated.setModel("Model 3");
        updated.setBrand(CarBrand.TESLA);
        updated.setType(CarType.SEDAN);
        updated.setPricePerHour(40);

        Car result = carService.updateCar(1L, updated);
        assertEquals("Model 3", result.getModel());
        assertEquals(40, result.getPricePerHour());
    }

    @Test
    void isCarAvailable_returnsFalseWhenOverlapsSameDate() {
        LocalDate date = LocalDate.of(2025, 6, 10);
        Reservation r1 = new Reservation();
        r1.setDate(date);
        r1.setStartTime(LocalTime.of(10, 0));
        r1.setEndTime(LocalTime.of(12, 0));
        when(reservationRepository.findByCarId(1L)).thenReturn(List.of(r1));

        boolean available = carService.isCarAvailable(1L, date, LocalTime.of(11, 0), LocalTime.of(13, 0));
        assertFalse(available);
    }

    @Test
    void createReservation_successful() {
        Long carId = 1L;
        LocalDate date = LocalDate.of(2025, 6, 10);
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 0);

        Car car = new Car();
        car.setId(carId);
        car.setPricePerHour(20);
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(reservationRepository.findByCarId(carId)).thenReturn(Collections.emptyList());

        User user = new User();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Set<Extra> extras = Set.of(Extra.GPS, Extra.INSURANCE);
        Reservation res = carService.createReservation("john", carId, extras, date, start, end);

        assertEquals(2, res.getExtras().size());
        assertEquals(user, res.getUser());
        assertEquals(car, res.getCar());
        assertEquals(date, res.getDate());
        assertEquals(40 + extras.stream().mapToDouble(Extra::getPrice).sum(), res.getTotalPrice());
    }
}
