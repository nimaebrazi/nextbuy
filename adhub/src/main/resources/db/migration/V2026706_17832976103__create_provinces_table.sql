CREATE TABLE provinces
(
    id         uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    country_id UUID         NOT NULL REFERENCES countries (id) ON DELETE RESTRICT,
    name       VARCHAR(100) NOT NULL,
    slug       VARCHAR(100) NOT NULL,
    centroid   GEOMETRY(POINT, 4326),
    boundary   GEOMETRY(MULTIPOLYGON, 4326),
    bbox       GEOMETRY(POLYGON, 4326), -- trigger-synced from boundary
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_provinces_country_name UNIQUE (country_id, name),
    CONSTRAINT uq_provinces_country_slug UNIQUE (country_id, slug)
);

CREATE INDEX idx_provinces_country_id ON provinces (country_id);
CREATE INDEX idx_provinces_boundary ON provinces USING GIST (boundary);
