package com.rentalapp.car_rental_system.enums;

public enum Extra {

    INSURANCE(20.0),
    CHILD_SEAT(15.0),
    GPS(10.0);

    private final double price;

    Extra(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
