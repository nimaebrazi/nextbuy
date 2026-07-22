package com.nextbuy.adhub.location.unit.domain.model;

import com.nextbuy.adhub.location.domain.model.GeoBoundary;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:GeoBoundaryTest")
class GeoBoundaryTest {

    private static final String UNIT_SQUARE_POLYGON = """
            {"type":"Polygon","coordinates":[[[0.0,0.0],[10.0,0.0],[10.0,10.0],[0.0,10.0],[0.0,0.0]]]}
            """;

    private static final String POLYGON_WITH_HOLE = """
            {"type":"Polygon","coordinates":[
                [[0.0,0.0],[10.0,0.0],[10.0,10.0],[0.0,10.0],[0.0,0.0]],
                [[4.0,4.0],[6.0,4.0],[6.0,6.0],[4.0,6.0],[4.0,4.0]]
            ]}
            """;

    private static final String MULTI_POLYGON = """
            {"type":"MultiPolygon","coordinates":[
                [[[0.0,0.0],[2.0,0.0],[2.0,2.0],[0.0,2.0],[0.0,0.0]]],
                [[[5.0,5.0],[7.0,5.0],[7.0,7.0],[5.0,7.0],[5.0,5.0]]]
            ]}
            """;

    @Test
    @DisplayName("It should accept Polygon and MultiPolygon geoJson.")
    void should_Create_When_TypeIsPolygonal() {
        assertThat(GeoBoundary.of(UNIT_SQUARE_POLYGON).geoJson()).isEqualTo(UNIT_SQUARE_POLYGON);
        assertThat(GeoBoundary.of(MULTI_POLYGON)).isNotNull();
    }

    @Test
    @DisplayName("It should reject blank and non-polygonal geoJson.")
    void should_Throw_When_GeoJsonInvalid() {
        assertThatThrownBy(() -> GeoBoundary.of("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("geoJson is required");

        assertThatThrownBy(() -> GeoBoundary.of("{\"type\":\"Point\",\"coordinates\":[1.0,2.0]}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("geoJson root type must be Polygon or MultiPolygon");
    }

    @Test
    @DisplayName("It should reject geoJson when coordinates are missing.")
    void should_Throw_When_CoordinatesMissing() {
        assertThatThrownBy(() -> GeoBoundary.of("{\"type\":\"Polygon\"}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("geoJson must contain coordinates");
    }

    @Test
    @DisplayName("It should reject geoJson when a ring is too short.")
    void should_Throw_When_RingTooShort() {
        String shortRing = """
                {"type":"Polygon","coordinates":[[[0.0,0.0],[1.0,0.0],[0.0,0.0]]]}
                """;

        assertThatThrownBy(() -> GeoBoundary.of(shortRing))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("It should reject geoJson when a coordinate pair is malformed.")
    void should_Throw_When_CoordinatePairMalformed() {
        String malformed = """
                {"type":"Polygon","coordinates":[[[0.0],[10.0,0.0],[10.0,10.0],[0.0,10.0],[0.0,0.0]]]}
                """;

        assertThatThrownBy(() -> GeoBoundary.of(malformed))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("It should return null from ofNullable for null or blank input.")
    void should_ReturnNull_When_OfNullableGivenNothing() {
        assertThat(GeoBoundary.ofNullable(null)).isNull();
        assertThat(GeoBoundary.ofNullable("  ")).isNull();
    }

    @Test
    @DisplayName("It should contain points inside a polygon and reject points outside.")
    void should_DetectContainment_When_Polygon() {
        GeoBoundary boundary = GeoBoundary.of(UNIT_SQUARE_POLYGON);

        assertThat(boundary.contains(GeoPoint.of(5.0, 5.0))).isTrue();
        assertThat(boundary.contains(GeoPoint.of(11.0, 5.0))).isFalse();
        assertThat(boundary.contains(GeoPoint.of(5.0, -1.0))).isFalse();
        assertThat(boundary.contains(null)).isFalse();
    }

    @Test
    @DisplayName("It should treat points inside a hole as outside.")
    void should_ExcludeHoles_When_PolygonHasInnerRing() {
        GeoBoundary boundary = GeoBoundary.of(POLYGON_WITH_HOLE);

        assertThat(boundary.contains(GeoPoint.of(5.0, 5.0))).isFalse();
        assertThat(boundary.contains(GeoPoint.of(2.0, 2.0))).isTrue();
    }

    @Test
    @DisplayName("It should check all polygons of a MultiPolygon.")
    void should_DetectContainment_When_MultiPolygon() {
        GeoBoundary boundary = GeoBoundary.of(MULTI_POLYGON);

        assertThat(boundary.contains(GeoPoint.of(1.0, 1.0))).isTrue();
        assertThat(boundary.contains(GeoPoint.of(6.0, 6.0))).isTrue();
        assertThat(boundary.contains(GeoPoint.of(3.5, 3.5))).isFalse();
    }
}
