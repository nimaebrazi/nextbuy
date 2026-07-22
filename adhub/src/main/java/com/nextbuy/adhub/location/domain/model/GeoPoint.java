package com.nextbuy.adhub.location.domain.model;

public record GeoPoint(double latitude, double longitude) {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    public GeoPoint {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    public static GeoPoint of(double latitude, double longitude) {
        return new GeoPoint(latitude, longitude);
    }

    /**
     * Both null → null (location without coordinates).
     * One null → error (lat/long must be a pair).
     */
    public static GeoPoint ofNullable(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) {
            return null;
        }
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude must both be provided or both absent");
        }
        return of(latitude, longitude);
    }

    private static void validateLatitude(double latitude) {
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException(
                    "Latitude must be between " + MIN_LATITUDE + " and " + MAX_LATITUDE
            );
        }
    }

    private static void validateLongitude(double longitude) {
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException(
                    "Longitude must be between " + MIN_LONGITUDE + " and " + MAX_LONGITUDE
            );
        }
    }

    @Override
    public String toString() {
        return String.format("%f,%f", latitude, longitude);
    }
}