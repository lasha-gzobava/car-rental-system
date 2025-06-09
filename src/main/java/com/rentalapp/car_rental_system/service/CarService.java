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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public List<Car> getAllCars() {
        List<Car> all = carRepository.findAll();
        System.out.println("LOADED CARS: " + all); // See console output
        return all;
    }



    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }



    public Car addCar(Car car) {

        String slug = (car.getBrand() + "-" + car.getModel()).toLowerCase().replaceAll("[^a-z0-9]", "-");
        car.setSlug(slug);

        return carRepository.save(car);
    }

    public Car updateCar(Long id, Car updatedCar) {
        Car car = getCarById(id);


        car.setModel(updatedCar.getModel());
        car.setBrand(updatedCar.getBrand());
        car.setType(updatedCar.getType());
        car.setPricePerHour(updatedCar.getPricePerHour());


        String slug = (car.getBrand() + "-" + car.getModel()).toLowerCase().replaceAll("[^a-z0-9]", "-");
        car.setSlug(slug);

        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public void setAvailability(Long id, boolean available) {
        Car car = getCarById(id);

        carRepository.save(car);
    }

    public String generateSlug(String model) {
        return model.toLowerCase().replaceAll(" ", "-");
    }

    public Car getCarBySlug(String slug) {
        return carRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }


    public boolean isCarAvailable(Long carId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Reservation> reservations = reservationRepository.findByCarId(carId);

        for (Reservation reservation : reservations) {
            if (!reservation.getDate().equals(date)) continue;

            if (!(endTime.isBefore(reservation.getStartTime()) || startTime.isAfter(reservation.getEndTime()))) {
                return false; // Conflict on the same day
            }
        }

        return true; // No conflict on that date
    }


    public Reservation createReservation(String username, Long carId, Set<Extra> extras,
                                         LocalDate date, LocalTime startTime, LocalTime endTime) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        if (!isCarAvailable(carId, date, startTime, endTime)) {
            throw new IllegalArgumentException("Car is not available for the selected time");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        double extraPrice = extras.stream()
                .mapToDouble(Extra::getPrice).sum();

        double totalPrice = extraPrice + (ChronoUnit.HOURS.between(startTime, endTime) * car.getPricePerHour());

        Reservation reservation = new Reservation();
        reservation.setCar(car);
        reservation.setUser(user);
        reservation.setDate(date); // ✅ set booking date
        reservation.setExtras(extras);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setTotalPrice(totalPrice);

        return reservationRepository.save(reservation);
    }





}

