
# Car Rental Management System

A Java Spring Boot web application for managing car rentals, users, and reservations.

## Features

- User registration and login
- Browse and rent cars
- Prevent double-booking by time slot
- Admin panel for managing cars and users
- User reservation history with cancel option

## Tech Stack

- Java, Spring Boot, Spring Security
- Thymeleaf, HTML, CSS
- PostgreSQL, Hibernate (JPA)
- Maven

## Setup

1. Create a PostgreSQL database named `car_reservation`.
2. Create a `.env` file in the project root:

```

DB\_URL=jdbc\:postgresql://localhost:5432/car\_reservation
DB\_USERNAME=your\_username
DB\_PASSWORD=your\_password

```

3. Run the app:

```

mvn spring-boot\:run

```

## License

MIT

