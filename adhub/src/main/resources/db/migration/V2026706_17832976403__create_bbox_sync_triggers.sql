-- =========================================================
-- TRIGGER: auto-sync bbox (geometry + plain floats) from boundary
-- =========================================================
CREATE OR REPLACE FUNCTION sync_bbox() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.boundary IS NOT NULL THEN
        NEW.bbox := ST_Envelope(NEW.boundary);
    END IF;
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_countries_bbox
BEFORE INSERT OR UPDATE OF boundary ON countries
FOR EACH ROW EXECUTE FUNCTION sync_bbox();

CREATE TRIGGER trg_provinces_bbox
BEFORE INSERT OR UPDATE OF boundary ON provinces
FOR EACH ROW EXECUTE FUNCTION sync_bbox();

CREATE OR REPLACE FUNCTION sync_bbox_with_floats() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.boundary IS NOT NULL THEN
        NEW.bbox := ST_Envelope(NEW.boundary);
        NEW.bbox_min_lng := ST_XMin(NEW.bbox);
        NEW.bbox_min_lat := ST_YMin(NEW.bbox);
        NEW.bbox_max_lng := ST_XMax(NEW.bbox);
        NEW.bbox_max_lat := ST_YMax(NEW.bbox);
    END IF;
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cities_bbox
BEFORE INSERT OR UPDATE OF boundary ON cities
FOR EACH ROW EXECUTE FUNCTION sync_bbox_with_floats();

CREATE TRIGGER trg_neighbourhoods_bbox
BEFORE INSERT OR UPDATE OF boundary ON neighbourhoods
FOR EACH ROW EXECUTE FUNCTION sync_bbox_with_floats();
