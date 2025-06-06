package com.rentalapp.car_rental_system.entity;

import com.rentalapp.car_rental_system.enums.Extra;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Car car;


    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ElementCollection(targetClass = Extra.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "reservation_extras", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "extra")
    private Set<Extra> extras;

    @Column(nullable = false)
    private double totalPrice;

}
