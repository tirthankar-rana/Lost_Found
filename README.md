# LostFound

A Spring Boot application for managing lost and found items with user registration, item reporting, police report generation, and admin demo utilities.

## Features

- User registration and login via `/api/auth`
- Add lost and found items via `/api/items`
- Browse open lost/found items and filter by type and location
- View items by ID or by user
- Mark an item as resolved by its owner
- Generate police complaint drafts via `/api/police-reports`
- Admin summary and demo data generation endpoints via `/api/admin`
- Static frontend pages under `src/main/resources/static`

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- MySQL Connector/J
- Lombok (compile-time)
- Maven build system

## Prerequisites

- Java 17 SDK installed
- Maven installed or use included wrapper (`mvnw`, `mvnw.cmd`)
- MySQL server available

## Configuration

Update `src/main/resources/application.properties` with your database credentials and connection settings:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lost_found_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD_HERE
```

By default the app runs on port `10082`.

## Build and Run

From the project root:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Or using Maven directly:

```bash
mvn clean package
mvn spring-boot:run
```

## API Endpoints

### Authentication

- `POST /api/auth/register` — register a new user
- `POST /api/auth/login` — login existing user

### Items

- `POST /api/items` — create a lost/found item
- `GET /api/items` — list open items
- `GET /api/items?type=LOST|FOUND&location=...` — filter open items
- `GET /api/items/{id}` — get item details
- `GET /api/items/user/{userId}` — list items for a user
- `PUT /api/items/{id}/resolve?userId={userId}` — mark an item resolved

### Police Reports

- `POST /api/police-reports` — generate a police complaint draft

### Admin

- `GET /api/admin/summary` — retrieve counts of users/items/status
- `POST /api/admin/generate-duplicate-items?count=24` — generate demo items

## Frontend

The application includes static HTML pages in `src/main/resources/static` for:

- `index.html`
- `login.html`
- `register.html`
- `items.html`
- `add-item.html`
- `my-items.html`
- `admin.html`
- `report-police.html`

These pages are intended to interact with the REST API using client-side JavaScript.

## Notes

- The app currently uses `spring.jpa.hibernate.ddl-auto=update` for schema generation.
- Passwords are stored hashed using the `PasswordUtil` utility.
- The admin duplicate generator creates a demo user and sample items for UI testing.

## License

No license configured. Add a license file if you want to publish this repository publicly.
