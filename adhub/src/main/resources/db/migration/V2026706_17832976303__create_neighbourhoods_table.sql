CREATE TABLE neighbourhoods
(
    id         uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    city_id      UUID       NOT NULL REFERENCES cities (id) ON DELETE RESTRICT,
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

    CONSTRAINT uq_neighbourhoods_city_name UNIQUE (city_id, name),
    CONSTRAINT uq_neighbourhoods_city_slug UNIQUE (city_id, slug)
);

CREATE INDEX idx_neighbourhoods_city_id ON neighbourhoods (city_id);
CREATE INDEX idx_neighbourhoods_boundary ON neighbourhoods USING GIST (boundary);
