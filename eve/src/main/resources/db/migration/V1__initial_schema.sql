CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE diagnostic_centres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location VARCHAR(300) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE diagnostic_tests (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE centre_tests (
    id BIGSERIAL PRIMARY KEY,

    centre_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,

    price NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_centre_tests_centre
        FOREIGN KEY (centre_id)
        REFERENCES diagnostic_centres(id),

    CONSTRAINT fk_centre_tests_test
        FOREIGN KEY (test_id)
        REFERENCES diagnostic_tests(id),

    CONSTRAINT uq_centre_test
        UNIQUE (centre_id, test_id),

    CONSTRAINT chk_centre_test_price
        CHECK (price > 0)
);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    centre_test_id BIGINT NOT NULL,

    appointment_at TIMESTAMP NOT NULL,

    amount NUMERIC(10,2) NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_booking_centre_test
        FOREIGN KEY (centre_test_id)
        REFERENCES centre_tests(id),

    CONSTRAINT chk_booking_status
        CHECK (
            status IN (
                'PENDING',
                'CONFIRMED',
                'FAILED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_booking_amount
        CHECK (amount > 0)
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,

    booking_id BIGINT NOT NULL UNIQUE,

    provider_payment_id VARCHAR(255) NOT NULL UNIQUE,

    amount NUMERIC(10,2) NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (booking_id)
        REFERENCES bookings(id),

    CONSTRAINT chk_payment_status
        CHECK (
            status IN ('SUCCESS', 'FAILED')
        ),

    CONSTRAINT chk_payment_amount
        CHECK (amount > 0)
);

CREATE TABLE webhook_events (
    id BIGSERIAL PRIMARY KEY,

    event_id VARCHAR(255) NOT NULL UNIQUE,

    event_type VARCHAR(100) NOT NULL,

    payload TEXT NOT NULL,

    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
