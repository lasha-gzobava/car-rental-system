package com.rentalapp.car_rental_system.repository;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.enums.CarBrand;
import com.rentalapp.car_rental_system.enums.CarType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Test
    public void shouldSaveAndFindCarBySlug() {
        Car car = Car.builder()
                .licensePlate("ABC123")
                .model("Civic")
                .brand(CarBrand.HONDA)
                .type(CarType.SEDAN)
                .pricePerHour(25)
                .slug("honda-civic")
                .build();

        carRepository.save(car);
        Optional<Car> found = carRepository.findBySlug("honda-civic");

        assertThat(found).isPresent();
        assertThat(found.get().getLicensePlate()).isEqualTo("ABC123");
    }
}
