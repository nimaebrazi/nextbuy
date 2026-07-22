CREATE TABLE cities
(
    id         uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    province_id  UUID       NOT NULL REFERENCES provinces (id) ON DELETE RESTRICT,
    name         VARCHAR(100) NOT NULL,
    slug         VARCHAR(100) NOT NULL,
    centroid     GEOMETRY(POINT, 4326),
    boundary     GEOMETRY(MULTIPOLYGON, 4326),
    bbox         GEOMETRY(POLYGON, 4326), -- trigger-synced from boundary
    bbox_min_lng DOUBLE PRECISION,        -- trigger-synced from bbox
    bbox_min_lat DOUBLE PRECISION,
    bbox_max_lng DOUBLE PRECISION,
    bbox_max_lat DOUBLE PRECISION,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_cities_province_name UNIQUE (province_id, name),
    CONSTRAINT uq_cities_province_slug UNIQUE (province_id, slug),
    CONSTRAINT uq_cities_slug UNIQUE (slug)
);

CREATE INDEX idx_cities_province_id ON cities (province_id);
CREATE INDEX idx_cities_boundary ON cities USING GIST (boundary);
