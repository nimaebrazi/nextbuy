package com.nextbuy.adhub.location.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Administrative-area boundary as a GeoJSON geometry string
 * (root type {@code Polygon} or {@code MultiPolygon}).
 *
 * <p>Pure Java — GeoJSON is kept as an opaque validated string plus a parsed
 * coordinate tree used for point-in-polygon checks. Conversion to/from spatial
 * database types happens in the infrastructure layer.</p>
 */
public record GeoBoundary(String geoJson) {

    private static final Pattern TYPE_PATTERN =
            Pattern.compile("\"type\"\\s*:\\s*\"(Polygon|MultiPolygon)\"");

    public GeoBoundary {
        if (geoJson == null || geoJson.isBlank()) {
            throw new IllegalArgumentException("geoJson is required");
        }
        if (!TYPE_PATTERN.matcher(geoJson).find()) {
            throw new IllegalArgumentException("geoJson root type must be Polygon or MultiPolygon");
        }
        // fail fast on malformed coordinates
        parsePolygons(geoJson);
    }

    public static GeoBoundary of(String geoJson) {
        return new GeoBoundary(geoJson);
    }

    public static GeoBoundary ofNullable(String geoJson) {
        return geoJson == null || geoJson.isBlank() ? null : new GeoBoundary(geoJson);
    }

    /**
     * Ray-casting point-in-polygon — pure Java, no PostGIS.
     * A point inside a hole of a polygon is considered outside.
     */
    public boolean contains(GeoPoint point) {
        if (point == null) {
            return false;
        }
        for (List<List<double[]>> polygon : parsePolygons(geoJson)) {
            if (polygon.isEmpty()) {
                continue;
            }
            if (!inRing(point, polygon.get(0))) {
                continue; // not in the outer ring
            }
            boolean inHole = false;
            for (int i = 1; i < polygon.size(); i++) {
                if (inRing(point, polygon.get(i))) {
                    inHole = true;
                    break;
                }
            }
            if (!inHole) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // GeoJSON coordinate parsing (minimal, dependency-free)
    // -------------------------------------------------------------------------

    /**
     * Returns polygons → rings → positions ({@code [lng, lat]}).
     * A Polygon geometry is promoted to a single-element MultiPolygon structure.
     */
    private static List<List<List<double[]>>> parsePolygons(String geoJson) {
        Matcher typeMatcher = TYPE_PATTERN.matcher(geoJson);
        if (!typeMatcher.find()) {
            throw new IllegalArgumentException("geoJson root type must be Polygon or MultiPolygon");
        }
        String type = typeMatcher.group(1);

        int coordinatesKey = geoJson.indexOf("\"coordinates\"");
        if (coordinatesKey < 0) {
            throw new IllegalArgumentException("geoJson must contain coordinates");
        }
        int start = geoJson.indexOf('[', coordinatesKey);
        if (start < 0) {
            throw new IllegalArgumentException("geoJson coordinates are malformed");
        }

        Object tree = new JsonArrayParser(geoJson, start).parse();

        List<List<List<double[]>>> polygons = new ArrayList<>();
        if (type.equals("Polygon")) {
            polygons.add(toPolygon(tree));
        } else {
            for (Object polygon : asList(tree, "MultiPolygon coordinates")) {
                polygons.add(toPolygon(polygon));
            }
        }
        return polygons;
    }

    private static List<List<double[]>> toPolygon(Object node) {
        List<List<double[]>> rings = new ArrayList<>();
        for (Object ring : asList(node, "polygon rings")) {
            List<double[]> positions = new ArrayList<>();
            for (Object position : asList(ring, "ring positions")) {
                List<?> pair = asList(position, "coordinate pair");
                if (pair.size() < 2 || !(pair.get(0) instanceof Double lng) || !(pair.get(1) instanceof Double lat)) {
                    throw new IllegalArgumentException("geoJson coordinate pair must be [lng, lat] numbers");
                }
                positions.add(new double[]{lng, lat});
            }
            if (positions.size() < 4) {
                throw new IllegalArgumentException("geoJson ring must have at least 4 positions");
            }
            rings.add(positions);
        }
        if (rings.isEmpty()) {
            throw new IllegalArgumentException("geoJson polygon must have at least one ring");
        }
        return rings;
    }

    private static List<?> asList(Object node, String what) {
        if (!(node instanceof List<?> list)) {
            throw new IllegalArgumentException("geoJson is malformed: expected array for " + what);
        }
        return list;
    }

    private static boolean inRing(GeoPoint point, List<double[]> ring) {
        double x = point.longitude();
        double y = point.latitude();
        boolean inside = false;
        for (int i = 0, j = ring.size() - 1; i < ring.size(); j = i++) {
            double xi = ring.get(i)[0];
            double yi = ring.get(i)[1];
            double xj = ring.get(j)[0];
            double yj = ring.get(j)[1];
            boolean intersects = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersects) {
                inside = !inside;
            }
        }
        return inside;
    }

    /** Parses a JSON array of nested arrays and numbers (all a coordinates node can contain). */
    private static final class JsonArrayParser {
        private final String input;
        private int pos;

        private JsonArrayParser(String input, int pos) {
            this.input = input;
            this.pos = pos;
        }

        private Object parse() {
            skipWhitespace();
            if (charAt() == '[') {
                pos++;
                List<Object> items = new ArrayList<>();
                skipWhitespace();
                if (charAt() == ']') {
                    pos++;
                    return items;
                }
                while (true) {
                    items.add(parse());
                    skipWhitespace();
                    char c = charAt();
                    if (c == ',') {
                        pos++;
                    } else if (c == ']') {
                        pos++;
                        return items;
                    } else {
                        throw new IllegalArgumentException("geoJson coordinates are malformed at position " + pos);
                    }
                }
            }
            return parseNumber();
        }

        private Double parseNumber() {
            int start = pos;
            while (pos < input.length()) {
                char c = input.charAt(pos);
                if (c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E' || Character.isDigit(c)) {
                    pos++;
                } else {
                    break;
                }
            }
            if (start == pos) {
                throw new IllegalArgumentException("geoJson coordinates are malformed at position " + pos);
            }
            try {
                return Double.parseDouble(input.substring(start, pos));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("geoJson coordinates contain an invalid number", e);
            }
        }

        private char charAt() {
            if (pos >= input.length()) {
                throw new IllegalArgumentException("geoJson coordinates are truncated");
            }
            return input.charAt(pos);
        }

        private void skipWhitespace() {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }
        }
    }
}
