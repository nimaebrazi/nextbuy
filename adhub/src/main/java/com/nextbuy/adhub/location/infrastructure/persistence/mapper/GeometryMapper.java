package com.nextbuy.adhub.location.infrastructure.persistence.mapper;

import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.GeoBoundary;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

/**
 * JTS ↔ domain value object conversion at the infrastructure boundary.
 * All methods are null-safe ({@code null} in → {@code null} out) so JTS types
 * never leak into the domain and domain VOs never leak into JPA entities.
 */
public final class GeometryMapper {

    private static final int SRID = 4326;
    private static final GeometryFactory FACTORY = new GeometryFactory(new PrecisionModel(), SRID);

    private GeometryMapper() {
    }

    // -------------------------------------------------------------------------
    // GeoPoint ↔ JTS Point
    // -------------------------------------------------------------------------

    public static Point toJts(GeoPoint geoPoint) {
        if (geoPoint == null) {
            return null;
        }
        return FACTORY.createPoint(new Coordinate(geoPoint.longitude(), geoPoint.latitude()));
    }

    public static GeoPoint toGeoPoint(Point point) {
        if (point == null) {
            return null;
        }
        return GeoPoint.of(point.getY(), point.getX());
    }

    // -------------------------------------------------------------------------
    // GeoBoundary (GeoJSON string) ↔ JTS MultiPolygon
    // -------------------------------------------------------------------------

    public static MultiPolygon toJts(GeoBoundary boundary) {
        if (boundary == null) {
            return null;
        }
        Geometry geometry;
        try {
            geometry = new GeoJsonReader(FACTORY).read(boundary.geoJson());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid boundary GeoJSON", e);
        }
        geometry.setSRID(SRID);
        if (geometry instanceof MultiPolygon multiPolygon) {
            return multiPolygon;
        }
        if (geometry instanceof Polygon polygon) {
            MultiPolygon promoted = FACTORY.createMultiPolygon(new Polygon[]{polygon});
            promoted.setSRID(SRID);
            return promoted;
        }
        throw new IllegalArgumentException(
                "Boundary GeoJSON must be a Polygon or MultiPolygon, got " + geometry.getGeometryType());
    }

    public static GeoBoundary toGeoBoundary(MultiPolygon geometry) {
        if (geometry == null) {
            return null;
        }
        GeoJsonWriter writer = new GeoJsonWriter();
        writer.setEncodeCRS(false);
        return GeoBoundary.of(writer.write(geometry));
    }

    // -------------------------------------------------------------------------
    // BoundingBox ↔ JTS Polygon envelope
    // -------------------------------------------------------------------------

    public static Polygon toJts(BoundingBox bbox) {
        if (bbox == null) {
            return null;
        }
        Envelope envelope = new Envelope(bbox.west(), bbox.east(), bbox.south(), bbox.north());
        Polygon polygon = (Polygon) FACTORY.toGeometry(envelope);
        polygon.setSRID(SRID);
        return polygon;
    }

    public static BoundingBox toBoundingBox(Polygon polygon) {
        if (polygon == null) {
            return null;
        }
        Envelope envelope = polygon.getEnvelopeInternal();
        return BoundingBox.of(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
    }
}
