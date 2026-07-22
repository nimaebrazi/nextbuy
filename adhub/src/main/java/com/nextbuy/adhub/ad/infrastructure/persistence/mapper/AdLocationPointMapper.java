package com.nextbuy.adhub.ad.infrastructure.persistence.mapper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

final class AdLocationPointMapper {

    private static final GeometryFactory FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private AdLocationPointMapper() {
    }

    static Point toJts(double latitude, double longitude) {
        return FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    static double[] fromJts(Point point) {
        if (point == null) {
            return null;
        }
        return new double[]{point.getY(), point.getX()};
    }
}
