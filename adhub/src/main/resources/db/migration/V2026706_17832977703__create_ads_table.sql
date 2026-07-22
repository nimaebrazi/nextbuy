CREATE TYPE ad_status AS ENUM ('DRAFT', 'ACTIVE', 'REJECTED', 'EXPIRED', 'DELETED', 'PENDING_MODERATION', 'SUSPENDED');

CREATE TABLE ads
(
    id                uuid PRIMARY KEY        DEFAULT gen_random_uuid(),
    owner_id          BIGINT         NOT NULL,                      -- reference to passport-service user, no FK across services
    title             VARCHAR(200)   NOT NULL,
    description       TEXT,
    price_amount      NUMERIC(19, 2) NULL CHECK (price_amount > 0), -- domain Price is the primary validator
    price_currency    CHAR(3)        NULL,
    category_id       UUID           NOT NULL REFERENCES categories (id) ON DELETE RESTRICT,
    status            ad_status      NOT NULL,
    rejection_reason  TEXT           NULL,
    suspension_reason TEXT           NULL,
    suspended_at      TIMESTAMPTZ    NULL     DEFAULT NULL,
    expires_at        TIMESTAMPTZ    NULL     DEFAULT NULL,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    deleted_at        TIMESTAMPTZ    NULL     DEFAULT NULL,
    rejected_at       TIMESTAMPTZ    NULL     DEFAULT NULL,
    version           BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_ads_expires_at ON ads (expires_at);
CREATE INDEX idx_ads_owner_status ON ads (owner_id, status);
CREATE INDEX idx_ads_category_status ON ads (category_id, status);
CREATE INDEX idx_ads_active_expires ON ads (expires_at) WHERE status = 'ACTIVE';
