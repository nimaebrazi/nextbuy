CREATE TABLE countries
(
    id         uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL,
    iso_code   CHAR(2)      NOT NULL,
    slug       VARCHAR(100) NOT NULL,
    centroid   GEOMETRY(POINT, 4326),
    boundary   GEOMETRY(MULTIPOLYGON, 4326),
    bbox       GEOMETRY(POLYGON, 4326), -- trigger-synced from boundary
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_countries_iso_code UNIQUE (iso_code),
    CONSTRAINT uq_countries_slug UNIQUE (slug)
);

CREATE INDEX idx_countries_boundary ON countries USING GIST (boundary);
