package com.nextbuy.adhub.location.domain.model;


public record BoundingBox(double west, double south, double east, double north) {

    public BoundingBox {
        validateLongitude(west, "west");
        validateLongitude(east, "east");
        validateLatitude(south, "south");
        validateLatitude(north, "north");
        if (west >= east) {
            throw new IllegalArgumentException("west must be less than east");
        }
        if (south >= north) {
            throw new IllegalArgumentException("south must be less than north");
        }
    }

    public static BoundingBox of(double west, double south, double east, double north) {
        return new BoundingBox(west, south, east, north);
    }

    /**
     * All null → null. Partially null → error (bbox components must come together).
     */
    public static BoundingBox ofNullable(Double west, Double south, Double east, Double north) {
        if (west == null && south == null && east == null && north == null) {
            return null;
        }
        if (west == null || south == null || east == null || north == null) {
            throw new IllegalArgumentException("All bbox components must be provided or all absent");
        }
        return of(west, south, east, north);
    }

    public static BoundingBox parse(String csv) {
        if (csv == null || csv.isBlank()) {
            throw new IllegalArgumentException("bbox is required");
        }
        String[] parts = csv.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("bbox must be 'west,south,east,north'");
        }
        try {
            return of(
                    Double.parseDouble(parts[0].trim()),
                    Double.parseDouble(parts[1].trim()),
                    Double.parseDouble(parts[2].trim()),
                    Double.parseDouble(parts[3].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("bbox components must be numbers", e);
        }
    }

    public String toCsv() {
        return west + "," + south + "," + east + "," + north;
    }

    public boolean contains(GeoPoint point) {
        if (point == null) {
            return false;
        }
        return point.longitude() >= west && point.longitude() <= east
                && point.latitude() >= south && point.latitude() <= north;
    }

    private static void validateLatitude(double value, String name) {
        if (value < -90.0 || value > 90.0) {
            throw new IllegalArgumentException(name + " must be between -90 and 90");
        }
    }

    private static void validateLongitude(double value, String name) {
        if (value < -180.0 || value > 180.0) {
            throw new IllegalArgumentException(name + " must be between -180 and 180");
        }
    }
}
