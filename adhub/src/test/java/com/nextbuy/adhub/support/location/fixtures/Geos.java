package com.nextbuy.adhub.support.location.fixtures;

import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.GeoBoundary;
import com.nextbuy.adhub.location.domain.model.GeoPoint;

import static com.nextbuy.adhub.support.shared.Fakers.faker;

public final class Geos {

    private Geos() {
    }

    public static GeoPoint randomPoint() {
        double latitude = faker().number().randomDouble(6, -89, 89);
        double longitude = faker().number().randomDouble(6, -179, 179);
        return GeoPoint.of(latitude, longitude);
    }

    public static BoundingBox randomBbox() {
        double west = faker().number().randomDouble(4, -179, 177);
        double south = faker().number().randomDouble(4, -89, 87);
        return BoundingBox.of(west, south, west + 1.0, south + 1.0);
    }

    /** Square polygon boundary around a center point. */
    public static GeoBoundary squareBoundary(double centerLat, double centerLng, double halfSize) {
        double w = centerLng - halfSize;
        double e = centerLng + halfSize;
        double s = centerLat - halfSize;
        double n = centerLat + halfSize;
        String geoJson = """
                {"type":"Polygon","coordinates":[[[%f,%f],[%f,%f],[%f,%f],[%f,%f],[%f,%f]]]}
                """.formatted(w, s, e, s, e, n, w, n, w, s).trim();
        return GeoBoundary.of(geoJson);
    }

    public static String randomSlug() {
        return faker().regexify("[a-z]{4,10}(-[a-z]{3,8})?");
    }
}
