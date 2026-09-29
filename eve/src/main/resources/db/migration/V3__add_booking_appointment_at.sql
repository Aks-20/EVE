ALTER TABLE bookings
    ADD COLUMN IF NOT EXISTS appointment_at TIMESTAMP;

UPDATE bookings
SET appointment_at = created_at
WHERE appointment_at IS NULL;

ALTER TABLE bookings
    ALTER COLUMN appointment_at SET NOT NULL;
