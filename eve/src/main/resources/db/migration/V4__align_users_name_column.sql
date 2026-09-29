-- Legacy DBs may have full_name from an older schema while the app uses name (V1).
UPDATE users
SET name = full_name
WHERE (name IS NULL OR btrim(name) = '')
  AND full_name IS NOT NULL;

ALTER TABLE users DROP COLUMN IF EXISTS full_name;

ALTER TABLE users
    ALTER COLUMN name SET NOT NULL;


ALTER TABLE users
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';