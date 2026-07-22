CREATE TABLE outbox_events
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(50)  NOT NULL, -- e.g. 'Ad'
    aggregate_id   UUID         NOT NULL,
    event_type     VARCHAR(100) NOT NULL, -- e.g. 'AdCreatedEvent'
    payload        JSONB        NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    published      BOOLEAN      NOT NULL DEFAULT false,
    published_at   TIMESTAMPTZ  NULL
);

CREATE INDEX idx_outbox_unpublished ON outbox_events (created_at, id) WHERE published = false;
