package com.rentalapp.car_rental_system.service;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.repository.CarRepository;
import com.rentalapp.car_rental_system.repository.ReservationRepository;
import com.rentalapp.car_rental_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private CarRepository carRepository;
    @InjectMocks private ReservationService reservationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReservation_successful() {
        Car car = new Car();
        car.setId(1L);
        car.setPricePerHour(10);

        User user = new User();
        user.setUsername("john");

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(reservationRepository.findByCarId(1L)).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Set<Extra> extras = Set.of(Extra.GPS);
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 0);

        Reservation res = reservationService.createReservation("john", 1L, extras, start, end);

        assertEquals(2 * 10 + Extra.GPS.getPrice(), res.getTotalPrice());
        assertEquals(user, res.getUser());
        assertEquals(car, res.getCar());
    }

    @Test
    void createReservation_conflict_throwsException() {
        Car car = new Car();
        car.setId(1L);
        car.setPricePerHour(15);
        User user = new User();
        Reservation existing = new Reservation();
        existing.setStartTime(LocalTime.of(10, 0));
        existing.setEndTime(LocalTime.of(12, 0));

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(reservationRepository.findByCarId(1L)).thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation("john", 1L, Set.of(), LocalTime.of(11, 0), LocalTime.of(13, 0)));
    }

    @Test
    void updateReservation_success() {
        Car car = new Car();
        car.setPricePerHour(10);
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCar(car);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LocalTime newStart = LocalTime.of(13, 0);
        LocalTime newEnd = LocalTime.of(15, 0);
        Set<Extra> extras = Set.of(Extra.INSURANCE);

        Reservation updated = reservationService.updateReservation(1L, newStart, newEnd, extras);

        assertEquals(newStart, updated.getStartTime());
        assertEquals(newEnd, updated.getEndTime());
        assertEquals(extras, updated.getExtras());
        assertEquals(2 * 10 + Extra.INSURANCE.getPrice(), updated.getTotalPrice());
    }

    @Test
    void deleteReservation_removesReservation() {
        Car car = new Car();
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCar(car);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.deleteReservation(1L);

        verify(reservationRepository).delete(reservation);
        verify(carRepository).save(car);
    }

    @Test
    void getReservation_found() {
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        assertEquals(reservation, reservationService.getReservation(1L));
    }

    @Test
    void getReservationsByUsername_returnsList() {
        User user = new User();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(reservationRepository.findByUser(user)).thenReturn(List.of(new Reservation(), new Reservation()));

        List<Reservation> res = reservationService.getReservationsByUsername("john");
        assertEquals(2, res.size());
    }

    @Test
    void getAllReservations_returnsAll() {
        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation()));
        assertEquals(1, reservationService.getAllReservations().size());
    }

    @Test
    void getReservationsByCarId_returnsList() {
        when(reservationRepository.findByCarId(1L)).thenReturn(List.of(new Reservation(), new Reservation()));
        assertEquals(2, reservationService.getReservationsByCarId(1L).size());
    }
}
