package com.nextbuy.adhub.location.unit.domain.model;

import com.nextbuy.adhub.location.domain.model.GeoPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:GeoPointTest")
class GeoPointTest {

    @Test
    @DisplayName("It should accept coordinates at the latitude and longitude boundaries.")
    void should_AcceptBoundaryCoordinates_When_ValuesAtMinMax() {
        assertThat(GeoPoint.of(-90.0, -180.0)).isEqualTo(new GeoPoint(-90.0, -180.0));
        assertThat(GeoPoint.of(90.0, 180.0)).isEqualTo(new GeoPoint(90.0, 180.0));
    }

    @Test
    @DisplayName("It should reject latitude outside the valid range.")
    void should_Throw_When_LatitudeOutOfRange() {
        assertThatThrownBy(() -> GeoPoint.of(-90.1, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between");

        assertThatThrownBy(() -> GeoPoint.of(90.1, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between");
    }

    @Test
    @DisplayName("It should reject longitude outside the valid range.")
    void should_Throw_When_LongitudeOutOfRange() {
        assertThatThrownBy(() -> GeoPoint.of(0.0, -180.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between");

        assertThatThrownBy(() -> GeoPoint.of(0.0, 180.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between");
    }

    @Test
    @DisplayName("It should return null from ofNullable when both coordinates are absent.")
    void should_ReturnNull_When_BothCoordinatesAbsent() {
        assertThat(GeoPoint.ofNullable(null, null)).isNull();
    }

    @Test
    @DisplayName("It should reject ofNullable when only one coordinate is provided.")
    void should_Throw_When_OnlyOneCoordinateProvided() {
        assertThatThrownBy(() -> GeoPoint.ofNullable(35.0, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Latitude and longitude must both be provided or both absent");

        assertThatThrownBy(() -> GeoPoint.ofNullable(null, 51.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Latitude and longitude must both be provided or both absent");
    }

    @Test
    @DisplayName("It should format latitude and longitude in toString.")
    void should_FormatLatLng_When_ToStringCalled() {
        GeoPoint point = GeoPoint.of(35.5, 51.4);

        assertThat(point.toString()).isEqualTo(String.format("%f,%f", 35.5, 51.4));
    }
}
