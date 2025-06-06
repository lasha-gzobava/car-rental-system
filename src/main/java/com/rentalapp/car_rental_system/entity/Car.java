package com.rentalapp.car_rental_system.entity;

import com.rentalapp.car_rental_system.enums.CarBrand;
import com.rentalapp.car_rental_system.enums.CarType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;


    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarBrand brand;


    @Enumerated(EnumType.STRING)
    private CarType type;

    @Column(nullable = false)
    private double pricePerHour;

    @Column(columnDefinition = "TEXT")
    private String description;




    @Column(nullable = false, unique = true)
    private String slug; // Slug for the URL

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}
