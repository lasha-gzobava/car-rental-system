package com.rentalapp.car_rental_system.repository;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.CarBrand;
import com.rentalapp.car_rental_system.enums.CarType;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Test
    public void shouldSaveAndFindReservationByCarIdAndUser() {
        User user = userRepository.save(User.builder()
                .firstName("Anna")
                .lastName("Smith")
                .username("anna")
                .email("anna@mail.com")
                .password("123456")  // Password must be at least 6 characters
                .role(Role.USER)
                .build());

        Car car = carRepository.save(Car.builder()
                .licensePlate("ZZZ111")
                .model("Model S")
                .brand(CarBrand.TESLA)
                .type(CarType.SEDAN)
                .pricePerHour(50)
                .slug("tesla-model-s")
                .build());

        Reservation res = Reservation.builder()
                .user(user)
                .car(car)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .extras(Set.of(Extra.GPS))
                .totalPrice(110)
                .date(java.time.LocalDate.of(2025, 6, 10)) // Required date
                .build();

        reservationRepository.save(res);

        List<Reservation> byCar = reservationRepository.findByCarId(car.getId());
        List<Reservation> byUser = reservationRepository.findByUser(user);

        assertThat(byCar).hasSize(1);
        assertThat(byUser).hasSize(1);
    }

}
