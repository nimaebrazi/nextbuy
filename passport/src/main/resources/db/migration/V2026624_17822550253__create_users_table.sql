CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    banned_at  TIMESTAMPTZ  NULL     DEFAULT NULL
);

CREATE INDEX idx_users_email on users (email);
