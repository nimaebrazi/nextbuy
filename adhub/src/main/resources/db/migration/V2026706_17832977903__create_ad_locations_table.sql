-- 1:1 extension table of ads: full location hierarchy denormalized for fast filters.
-- Hierarchy consistency (city ∈ province ∈ country, neighbourhood ∈ city) is
-- validated by the application layer at ad-create time; no cross-column FK exists.
CREATE TABLE ad_locations
(
    ad_id            UUID PRIMARY KEY REFERENCES ads (id) ON DELETE CASCADE,
    country_id       UUID        NOT NULL REFERENCES countries (id) ON DELETE RESTRICT,
    province_id      UUID        NOT NULL REFERENCES provinces (id) ON DELETE RESTRICT,
    city_id          UUID        NOT NULL REFERENCES cities (id) ON DELETE RESTRICT,
    neighbourhood_id UUID REFERENCES neighbourhoods (id) ON DELETE RESTRICT,
    point            GEOMETRY(POINT, 4326), -- optional exact pin
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_ad_locations_country_id ON ad_locations (country_id);
CREATE INDEX idx_ad_locations_province_id ON ad_locations (province_id);
CREATE INDEX idx_ad_locations_city_id ON ad_locations (city_id);
CREATE INDEX idx_ad_locations_neighbourhood_id ON ad_locations (neighbourhood_id);
CREATE INDEX idx_ad_locations_point ON ad_locations USING GIST (point);
