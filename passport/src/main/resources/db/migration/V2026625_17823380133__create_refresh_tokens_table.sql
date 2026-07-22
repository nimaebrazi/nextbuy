CREATE TABLE refresh_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(64)              NOT NULL UNIQUE,
    user_id    BIGINT                   NOT NULL REFERENCES users (id),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE NULL,
    ip         VARCHAR(64),
    user_agent VARCHAR(512),
    device     VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_user_id on refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token_hash on refresh_tokens (token_hash);
