package com.rentalapp.car_rental_system.service;

import com.rentalapp.car_rental_system.entity.Car;
import com.rentalapp.car_rental_system.entity.Reservation;
import com.rentalapp.car_rental_system.entity.User;
import com.rentalapp.car_rental_system.enums.Extra;
import com.rentalapp.car_rental_system.repository.CarRepository;
import com.rentalapp.car_rental_system.repository.ReservationRepository;
import com.rentalapp.car_rental_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final Logger log = LoggerFactory.getLogger(CarService.class);
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public List<Car> getAllCars() {
        List<Car> all = carRepository.findAll();
        log.info("Loaded {} cars from the database", all.size());
        return all;
    }



    public Car getCarById(Long id) {
        log.debug("Fetching car with ID: {}", id);
        return carRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Car not found with ID: {}", id);
                    return new IllegalArgumentException("Car not found");
                });
    }




    public Car addCar(Car car) {

        String slug = (car.getBrand() + "-" + car.getModel()).toLowerCase().replaceAll("[^a-z0-9]", "-");
        car.setSlug(slug);
        Car saved = carRepository.save(car);
        log.info("Added new car: {} {} (ID: {})", car.getBrand(), car.getModel(), saved.getId());
        return saved;
    }

    public Car updateCar(Long id, Car updatedCar) {
        Car car = getCarById(id);


        car.setModel(updatedCar.getModel());
        car.setBrand(updatedCar.getBrand());
        car.setType(updatedCar.getType());
        car.setPricePerHour(updatedCar.getPricePerHour());


        String slug = (car.getBrand() + "-" + car.getModel()).toLowerCase().replaceAll("[^a-z0-9]", "-");
        car.setSlug(slug);

        Car saved = carRepository.save(car);
        log.info("Updated car ID: {}", id);
        return saved;
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
        log.info("Deleted car with ID: {}", id);
    }


    public void setAvailability(Long id, boolean available) {
        Car car = getCarById(id);
        carRepository.save(car);
        log.info("Set availability of car ID {} to {}", id, available);
    }

    public String generateSlug(String model) {
        return model.toLowerCase().replaceAll(" ", "-");
    }

    public Car getCarBySlug(String slug) {
        log.debug("Fetching car by slug: {}", slug);
        return carRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Car not found for slug {}", slug);
                    return new IllegalArgumentException("Car not found");
                });
    }


    public boolean isCarAvailable(Long carId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Reservation> reservations = reservationRepository.findByCarId(carId);
        for (Reservation reservation : reservations) {
            if (!reservation.getDate().equals(date)) continue;
            if (!(endTime.isBefore(reservation.getStartTime()) || startTime.isAfter(reservation.getEndTime()))) {
                log.info("Car ID {} is not available at {} on {}", carId, startTime, date);
                return false;
            }
        }
        log.info("Car ID {} is available at {} on {}", carId, startTime, date);
        return true;
    }


    public Reservation createReservation(String username, Long carId, Set<Extra> extras,
                                         LocalDate date, LocalTime startTime, LocalTime endTime) {
        log.info("Creating reservation for user '{}' and car ID {} on {}", username, carId, date);

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> {
                    log.warn("Car not found for reservation, ID: {}", carId);
                    return new IllegalArgumentException("Car not found");
                });

        if (!isCarAvailable(carId, date, startTime, endTime)) {
            log.warn("Reservation conflict for car ID {} on {}", carId, date);
            throw new IllegalArgumentException("Car is not available for the selected time");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found for reservation: {}", username);
                    return new IllegalArgumentException("User not found");
                });

        double extraPrice = extras.stream().mapToDouble(Extra::getPrice).sum();
        double totalPrice = extraPrice + (ChronoUnit.HOURS.between(startTime, endTime) * car.getPricePerHour());

        Reservation reservation = new Reservation();
        reservation.setCar(car);
        reservation.setUser(user);
        reservation.setDate(date);
        reservation.setExtras(extras);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setTotalPrice(totalPrice);

        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation created: ID {} | User: {} | Car: {}", saved.getId(), username, carId);
        return saved;
    }


}

