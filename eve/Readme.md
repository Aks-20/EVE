# EVE Healthcare API

A Spring Boot REST API for diagnostic centres, diagnostic tests, appointment bookings, and simulated payment processing.

## Features

- User signup and login with BCrypt password hashing
- JWT authentication and role-based authorization (`USER` and `ADMIN`)
- Optional environment-configured initial administrator
- Diagnostic centre and test management, including centre-specific pricing
- Booking creation, ownership checks, cancellation, and appointment validation
- Simulated payment processing
- PostgreSQL persistence and Flyway migrations
- OpenAPI documentation, request validation, and health endpoints

## Technology

| Component | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Migrations | Flyway |
| Security | Spring Security, JWT, BCrypt |
| API docs | Springdoc OpenAPI / Swagger UI |
| Build and tests | Maven, JUnit |
| Containers | Docker, Docker Compose |

## Architecture

```text
HTTP request
    -> Controller
    -> Service
    -> Repository
    -> PostgreSQL
```

Protected requests carry a JWT. The JWT filter loads the current user and grants authorities from the user's persisted role. Booking and payment services enforce ownership rules in addition to authentication.

## Project Structure

```text
src/main/java/com/eve/eve/
├── common/       Shared API response and exception handling
├── config/       Security, JWT, Jackson, and OpenAPI configuration
├── controller/   REST endpoints
├── dto/          Request and response records
├── entity/       JPA entities and enums
├── repository/   Spring Data repositories
└── service/      Application and business logic

src/main/resources/
├── application.properties
└── db/migration/ Flyway SQL migrations
```

## Prerequisites

- Java 21
- PostgreSQL, or Docker with Docker Compose

The default local configuration uses port `9090` and PostgreSQL at `localhost:5432`, database `EVE`, username `postgres`, and password `root`. Create the database before starting the application if it does not already exist:

```sql
CREATE DATABASE "EVE";
```

These are development defaults. Override database credentials and secrets for any shared or production environment.

## Run Locally

From the repository root, use the Maven wrapper:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

The application listens at `http://localhost:9090`.
Swagger UI is available at `http://localhost:9090/swagger-ui/index.html`.

## Configure an Initial Admin

Signup always creates a `USER`; it does not accept a role from the request. To create the initial admin, set all three variables before starting the application:

```powershell
$env:ADMIN_NAME = "EVE Admin"
$env:ADMIN_EMAIL = "admin@example.com"
$env:ADMIN_PASSWORD = "12345689"
.\mvnw.cmd spring-boot:run
```

The app creates that account with the `ADMIN` role. If the email already exists, it promotes the account only when the configured password matches the existing password. Partial configuration or a password mismatch stops startup with an error. Leave all three variables unset to disable admin bootstrap.

In Docker Compose, pass the `ADMIN_NAME`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD` variables to the `app` service's `environment` section if an admin account is required.

## Authentication and Authorization

1. Sign up a regular user with `POST /api/auth/signup`, or configure the initial admin as above.
2. Log in with `POST /api/auth/login`.
3. Copy `accessToken` from the response.
4. For protected endpoints, send `Authorization: Bearer <accessToken>`.

Read-only centre endpoints are public. Creating centres, tests, and centre-test price mappings requires `ADMIN`. Booking and payment endpoints require an authenticated user and apply ownership checks.

Unauthenticated protected requests return `401 Unauthorized`. An authenticated user without the required role receives `403 Forbidden`.

## API Endpoints

| Method | Endpoint | Access |
| --- | --- | --- |
| `POST` | `/api/auth/signup` | Public |
| `POST` | `/api/auth/login` | Public |
| `GET` | `/api/auth/me` | Authenticated |
| `GET` | `/api/centres` | Public |
| `GET` | `/api/centres/{id}` | Public |
| `POST` | `/api/centres` | Admin |
| `POST` | `/api/centres/{centreId}/tests` | Admin |
| `GET` | `/api/tests` | Authenticated |
| `POST` | `/api/tests` | Admin |
| `POST` | `/api/bookings` | Authenticated |
| `GET` | `/api/bookings` | Authenticated; own bookings |
| `GET` | `/api/bookings/{id}` | Authenticated; owner only |
| `POST` | `/api/bookings/{id}/cancel` | Authenticated; owner only |
| `POST` | `/api/payments` | Authenticated; booking owner |
| `GET` | `/api/health` | Public |
| `GET` | `/actuator/health` | Public |

All routes are relative to `http://localhost:9090` when running locally.

Diagnostic Centre
      │
      │ centre_id
      ▼
 CentreTest
      ▲
      │ test_id
      │
Diagnostic Test

CentreTest
      │
      │ centre_test_id
      ▼
   Booking

## Example API Flow

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "12345689"
}
```

Use the `accessToken` from the response as a Bearer token for admin operations.

### Create a Diagnostic Centre

```http
POST /api/centres
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "name": "Central Diagnostics",
  "location": "Springfield"
}
```

### Create a Diagnostic Test

```http
POST /api/tests
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "name": "Complete Blood Count",
  "description": "CBC blood test"
}
```

### Set the Centre's Test Price

```http
POST /api/centres/1/tests
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "testId": 1,
  "price": 500.00
}
```

### Create a Booking

```http
POST /api/bookings
Authorization: Bearer <userAccessToken>
Content-Type: application/json

{
  "centreTestId": 1,
  "appointmentAt": "2026-10-05T10:30:00"
}
```

The appointment must be in the future. The booking amount is copied from the centre-test price when the booking is created.

### Start Payment Processing

```http
POST /api/payments
Authorization: Bearer <userAccessToken>
Content-Type: application/json

{
  "bookingId": 1
}
```

## Database

The main tables are `users`, `diagnostic_centres`, `diagnostic_tests`, `centre_tests`, `bookings`, and `payments`.

- A centre and diagnostic test are linked through `centre_tests`, which stores the centre-specific price.
- A unique `(centre_id, test_id)` constraint prevents duplicate mappings.
- A booking stores a snapshot of the price at booking time.
- A booking has at most one payment record.
- Flyway migrations are in `src/main/resources/db/migration`.

## Configuration

Configuration is in `src/main/resources/application.properties`. Important settings include:

| Setting | Environment variable | Current default |
| --- | --- | --- |
| HTTP port | `SERVER_PORT` | `9090` |
| JWT signing key | `JWT_SECRET` | Development-only built-in key |
| JWT lifetime | Not separately environment-configured | `3600000` ms |
| Initial admin name | `ADMIN_NAME` | Disabled when unset |
| Initial admin email | `ADMIN_EMAIL` | Disabled when unset |
| Initial admin password | `ADMIN_PASSWORD` | Disabled when unset |

The current datasource settings are defined directly in `application.properties`; adjust them there or override them with Spring datasource environment properties for your environment. Never use the development database password or default JWT key in production.

## Docker Compose

Build and start the app and PostgreSQL:

```powershell
docker compose up --build
```

Stop the services:

```powershell
docker compose down
```

The Compose configuration exposes the API on port `9090` and PostgreSQL on port `5432`. The database data is stored in a named Docker volume.

## API Documentation and Health

- Swagger UI: `http://localhost:9090/swagger-ui.html`
- OpenAPI JSON: `http://localhost:9090/v3/api-docs`
- Application health: `http://localhost:9090/api/health`
- Actuator health: `http://localhost:9090/actuator/health`

## Payment and Booking Behavior

- Payment processing is simulated; no external payment provider is connected.
- Payment operations use transactions and lock booking records while updating state.
- Appointment availability and calendar conflict checking are outside the current scope.

## Testing

Run the test suite with the Maven wrapper:

```powershell
.\mvnw.cmd test
```

## Author

Akshat Gupta  
Backend Engineering Assignment - EVE Healthcare
