# EVE Healthcare — Backend Engineering Assignment

A production-oriented REST API for diagnostic centre management, test booking, and simulated payment processing.

The application is built using Spring Boot, Java, PostgreSQL, Spring Data JPA, Spring Security, JWT authentication, and Flyway database migrations.



## 1. Features

### Authentication

* User signup
* User login
* BCrypt password hashing
* JWT-based authentication
* Current-user endpoint
* Request validation
* Unauthorized request handling

### Diagnostic Centres

* Create diagnostic centre
* List diagnostic centres
* Get diagnostic centre by ID
* Associate diagnostic tests with centres
* Configure test-specific pricing

### Diagnostic Tests

* Create diagnostic test
* List diagnostic tests
* Test description support

### Bookings

* Authenticated users can create bookings
* Appointment date/time validation
* Booking automatically captures the centre-test price
* Booking ownership validation
* Booking status management
* Booking cancellation
* Users can view only their own bookings

### Payments

* Simulated payment processing
* SUCCESS / FAILED payment states
* Payment amount comes from booking
* Booking status automatically updated after payment
* Payment ownership validation

### Payment Webhooks

* Webhook endpoint
* Event ID based idempotency
* Duplicate webhook protection
* Database-level unique constraints
* Payment and booking consistency validation

### Engineering

* Global exception handling
* Bean Validation
* PostgreSQL
* Flyway migrations
* Transaction management
* Pessimistic locking for payment/booking operations
* Docker support
* Docker Compose
* OpenAPI / Swagger
* Actuator health endpoint
* Structured API responses

---

## 2. Technology Stack

| Technology      | Purpose                        |
| --------------- | ------------------------------ |
| Java 21         | Backend language               |
| Spring Boot     | Application framework          |
| Spring Web      | REST APIs                      |
| Spring Data JPA | Persistence                    |
| Hibernate       | ORM                            |
| PostgreSQL      | Database                       |
| Flyway          | Database migrations            |
| Spring Security | Authentication & authorization |
| JWT             | Stateless authentication       |
| BCrypt          | Password hashing               |
| Maven           | Build management               |
| Docker          | Containerization               |
| Swagger/OpenAPI | API documentation              |
| JUnit           | Testing                        |




## 3. Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Authentication:

```text
Client
  ↓
JWT
  ↓
Spring Security Filter
  ↓
Authenticated User
  ↓
Controller
```

Payment flow:

```text
Booking
   ↓
Payment Request
   ↓
Payment Service
   ↓
Simulated Payment Provider
   ↓
SUCCESS / FAILED
   ↓
Payment + Booking Status
```

Webhook flow:

```text
Payment Provider
       ↓
POST /api/payments/webhook
       ↓
Validate webhook secret
       ↓
Insert event using event_id
       ↓
Duplicate?
   ├── YES → Ignore
   └── NO
        ↓
     Process payment
        ↓
     Update booking
```

---

## 4. Project Structure

```text
src/main/java/com/evehealthcare
│
├── auth
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── centre
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── test
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── booking
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── payment
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── security
│   ├── JwtAuthenticationFilter
│   ├── JwtService
│   └── SecurityConfig
│
└── common
    ├── exception
    └── response
```

---

## 5. Database Schema

Main entities:

```text
users
  │
  │
  └──── bookings
             │
             │
             └──── centre_tests
                       │
                ┌──────┴──────┐
                ↓             ↓
       diagnostic_centres   diagnostic_tests


bookings
   │
   └──── payments


webhook_events
```

### Tables

#### users

```text
id
name
email
password_hash
role
created_at
```

#### diagnostic_centres

```text
id
name
location
created_at
```

#### diagnostic_tests

```text
id
name
description
created_at
```

#### centre_tests

```text
id
centre_id
test_id
price
```

A unique constraint on:

```text
(centre_id, test_id)
```

prevents duplicate test mappings.

#### bookings

```text
id
user_id
centre_test_id
appointment_at
amount
status
created_at
```

The booking amount is copied from `centre_tests.price` when the booking is created.

This prevents later price changes from changing an existing booking amount.

#### payments

```text
id
booking_id
provider_payment_id
amount
status
created_at
```

`booking_id` is unique so one booking cannot have multiple payment records.

#### webhook_events

```text
id
event_id
event_type
payload
processed_at
```

`event_id` is unique and provides database-level idempotency.

---

## 6. Booking State Machine

```text
             ┌─────────────┐
             │   PENDING   │
             └──────┬──────┘
                    │
          ┌─────────┴─────────┐
          ↓                   ↓
     CONFIRMED              FAILED
          ↑
          │
      SUCCESS

PENDING
   │
   ↓
CANCELLED
```

Only valid state transitions are allowed.

---

## 7. Configuration

Create PostgreSQL database:

```sql
CREATE DATABASE eve_healthcare;
```

Configure:

```properties
spring.application.name=eve-healthcare
server.port=8080

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/eve_healthcare}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

jwt.secret=${JWT_SECRET:change-this-to-a-long-secret-key}
jwt.expiration=${JWT_EXPIRATION:3600000}

webhook.secret=${WEBHOOK_SECRET:local-webhook-secret}

management.endpoints.web.exposure.include=health,info
```

For production, secrets should be supplied through environment variables or a secret manager.

---

## 8. Running Locally

### Clone

```bash
git clone <repository-url>

cd eve-healthcare
```

### Build

```bash
mvn clean install
```

### Run tests

```bash
mvn test
```

### Run application

```bash
mvn spring-boot:run
```

Application:

```text
http://localhost:9090
```

---

## 9. Running with Docker

Build and start:

```bash
docker compose up --build
```

Stop:

```bash
docker compose down
```

Application:

```text
http://localhost:9090
```

PostgreSQL:

```text
localhost:5432
```

---

## 10. API Documentation

Swagger UI:

```text
http://localhost:9090/swagger-ui.html
```

OpenAPI documentation:

```text
http://localhost:9090/v3/api-docs
```

---

## 11. API Endpoints

### Authentication

| Method | Endpoint           | Authentication |
| ------ | ------------------ | -------------- |
| POST   | `/api/auth/signup` | Public         |
| POST   | `/api/auth/login`  | Public         |
| GET    | `/api/auth/me`     | JWT            |

### Diagnostic Centres

| Method | Endpoint                        | Authentication |
| ------ | ------------------------------- | -------------- |
| POST   | `/api/centres`                  | Admin          |
| GET    | `/api/centres`                  | JWT            |
| GET    | `/api/centres/{id}`             | JWT            |
| POST   | `/api/centres/{centreId}/tests` | Admin          |

### Diagnostic Tests

| Method | Endpoint     | Authentication |
| ------ | ------------ | -------------- |
| POST   | `/api/tests` | Admin          |
| GET    | `/api/tests` | JWT            |

### Bookings

| Method | Endpoint                    | Authentication |
| ------ | --------------------------- | -------------- |
| POST   | `/api/bookings`             | JWT            |
| GET    | `/api/bookings`             | JWT            |
| GET    | `/api/bookings/{id}`        | JWT            |
| POST   | `/api/bookings/{id}/cancel` | JWT            |

### Payments

| Method | Endpoint                | Authentication |
| ------ | ----------------------- | -------------- |
| POST   | `/api/payments`         | JWT            |
| POST   | `/api/payments/webhook` | Webhook Secret |

### Health

| Method | Endpoint           |
| ------ | ------------------ |
| GET    | `/api/health`      |
| GET    | `/actuator/health` |

---

## 12. Example API Flow

### Step 1 — Signup

```http
POST /api/auth/signup
```

```json
{
  "name": "Akshat Gupta",
  "email": "akshat@example.com",
  "password": "Password@123"
}
```

### Step 2 — Login

```http
POST /api/auth/login
```

```json
{
  "email": "akshat@example.com",
  "password": "Password@123"
}
```

Copy the JWT token.

### Step 3 — Create Centre

```http
POST /api/centres
Authorization: Bearer <TOKEN>
```

```json
{
  "name": "Apollo Diagnostics",
  "location": "Gurugram"
}
```

### Step 4 — Create Test

```http
POST /api/tests
Authorization: Bearer <TOKEN>
```


{
  "name": "Complete Blood Count",
  "description": "CBC blood test"
}


### Step 5 — Configure Centre Test

http
POST /api/centres/1/tests
Authorization: Bearer <TOKEN>
```

json
{
  "testId": 1,
  "price": 500.00
}

### Step 6 — Create Booking

http
POST /api/bookings
Authorization: Bearer <TOKEN>


json
{
  "centreTestId": 1,
  "appointmentAt": "2026-10-05T10:30:00"
}


 Step 7 — Payment

http
POST /api/payments
Authorization: Bearer <TOKEN>


json
{
  "bookingId": 1
}


 Step 8 — Webhook

http
POST /api/payments/webhook
X-Webhook-Secret: local-webhook-secret



{
  "eventId": "evt_001",
  "eventType": "payment.success",
  "paymentId": "pay_external_001",
  "bookingId": 1,
  "status": "SUCCESS"
}


 13. Edge Cases

The application handles:

* Invalid request body
* Invalid email
* Short passwords
* Duplicate email
* Missing resources
* Invalid booking ID
* Invalid centre ID
* Invalid test ID
* Duplicate centre-test mapping
* Unauthorized booking access
* Payment for another user's booking
* Payment for non-existent booking
* Payment for already processed booking
* Payment for cancelled booking
* Duplicate webhook events
* Payment belonging to another booking
* Database constraint violations
* Invalid JWT
* Missing JWT
* Invalid webhook secret



## 14. Idempotency

Webhook idempotency is implemented using:


event_id

with a unique database constraint.

The webhook insertion uses PostgreSQL:

sql
INSERT ...
ON CONFLICT (event_id) DO NOTHING


Therefore, if the same webhook is received multiple times:


First request
     ↓
event inserted
     ↓
payment processed


Second request
     ↓
event already exists
     ↓
ignored


This prevents duplicate payment processing.



## 15. Concurrency Handling

Booking/payment operations use database transactions.

Payment and webhook processing lock the booking using:


PESSIMISTIC_WRITE


This prevents concurrent requests from simultaneously modifying the same booking state.

Example:

text
Request A ──┐
            │
            ├── Booking #1 locked
            │
Request B ──┘
                  ↓
             waits for A
                  ↓
             sees new state




## 16. Security

Implemented security measures:

* BCrypt password hashing
* JWT authentication
* Stateless sessions
* Role-based authorization
* Protected booking APIs
* Ownership validation
* Webhook secret validation
* Input validation
* No password returned through API responses
* Database constraints
* Environment-based secrets

---

## 17. Testing

Run:

bash
mvn test


Important scenarios:


Authentication
✓ Signup
✓ Duplicate signup
✓ Login
✓ Invalid credentials

Centres / Tests
✓ Create centre
✓ Create test
✓ Add test to centre
✓ Duplicate centre-test mapping

Bookings
✓ Create booking
✓ Price copied from centre-test
✓ Get own bookings
✓ Unauthorized booking access
✓ Cancel pending booking
✓ Cannot cancel confirmed booking

Payments
✓ Successful payment
✓ Failed payment
✓ Invalid booking
✓ Unauthorized payment

Webhooks
✓ Successful webhook
✓ Failed webhook
✓ Duplicate webhook
✓ Invalid booking
✓ Payment/booking mismatch

 18. Design Decisions

Why `CentreTest`?

A diagnostic test can be offered by multiple centres at different prices.

Therefore:

text
DiagnosticTest
      +
DiagnosticCentre
      ↓
CentreTest
      +
Price


Why store booking amount?

The price of a test may change later.

Example:

text
Today:
CBC = ₹500

Next month:
CBC = ₹700


An existing booking should remain:


₹500


Therefore the booking stores a snapshot of the price.






V1__initial_schema.sql
V2__add_user_role.sql
V3__future_change.sql




 19. Assumptions

1. Payment processing is simulated because no real payment provider is required.
2. Appointment availability/calendar conflict management is outside the assignment scope.
3. A booking has one payment record.
4. A centre-test combination is unique.
5. Booking price is captured when the booking is created.
6. Webhooks are trusted after webhook-secret validation.
7. JWT access tokens are stateless.
8. Centre and test management is restricted to administrators.
9. PostgreSQL is the target production database.








22. Health Check


GET /api/health


Expected:
json
{
  "success": true,
  "message": "Service is healthy",
  "data": "EVE Healthcare API"
}






24. Author

Akshat Gupta

Backend Engineering Assignment — EVE Healthcare
