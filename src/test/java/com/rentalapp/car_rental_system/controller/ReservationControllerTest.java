package com.rentalapp.car_rental_system.controller;

import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReservation() {
        Reservation reservation = new Reservation();
        when(reservationService.createReservation("user1", 1L, Set.of(Extra.GPS), LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .thenReturn(reservation);

        Reservation result = reservationController.createReservation(
                "user1", 1L, Set.of(Extra.GPS),
                LocalTime.of(10, 0), LocalTime.of(12, 0)
        );

        assertThat(result).isEqualTo(reservation);
        verify(reservationService).createReservation("user1", 1L, Set.of(Extra.GPS), LocalTime.of(10, 0), LocalTime.of(12, 0));
    }

    @Test
    void testCreateReservationWebSuccess() {
        Model model = new ConcurrentModel();
        String result = reservationController.createReservationWeb(
                "user1", 1L, Set.of(Extra.GPS),
                LocalTime.of(10, 0), 2, model
        );

        verify(reservationService).createReservation("user1", 1L, Set.of(Extra.GPS),
                LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertThat(result).isEqualTo("redirect:/cars");
    }

    @Test
    void testCreateReservationWebFailure() {
        Model model = new ConcurrentModel();
        doThrow(new IllegalArgumentException("Conflict")).when(reservationService).createReservation(
                any(), any(), any(), any(), any()
        );

        String result = reservationController.createReservationWeb(
                "user1", 1L, Set.of(Extra.GPS),
                LocalTime.of(10, 0), 2, model
        );

        assertThat(result).isEqualTo("rent-car");
        assertThat(model.getAttribute("error")).isEqualTo("Conflict");
    }

    @Test
    void testUpdateReservation() {
        Reservation updated = new Reservation();
        when(reservationService.updateReservation(eq(1L), any(), any(), any())).thenReturn(updated);

        Reservation result = reservationController.updateReservation(
                1L,
                Set.of(Extra.GPS), // ✅ fixed this line
                LocalTime.of(11, 0),
                LocalTime.of(13, 0)
        );

        assertThat(result).isEqualTo(updated);
    }


    @Test
    void testGetUserReservations() {
        List<Reservation> reservations = List.of(new Reservation(), new Reservation());
        when(reservationService.getReservationsByUsername("user1")).thenReturn(reservations);

        List<Reservation> result = reservationController.getUserReservations("user1");

        assertThat(result).hasSize(2);
        verify(reservationService).getReservationsByUsername("user1");
    }

    @Test
    void testCancelReservation() {
        reservationController.cancelReservation(1L);
        verify(reservationService).deleteReservation(1L);
    }

    @Test
    void testGetAllReservations() {
        List<Reservation> reservations = List.of(new Reservation());
        when(reservationService.getAllReservations()).thenReturn(reservations);

        List<Reservation> result = reservationController.getAllReservations();

        assertThat(result).isEqualTo(reservations);
    }
}
