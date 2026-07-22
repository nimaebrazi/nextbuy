package com.nextbuy.adhub.location.unit.domain.model;

import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:BoundingBoxTest")
class BoundingBoxTest {

    @Test
    @DisplayName("It should parse a west,south,east,north csv.")
    void should_Parse_When_CsvIsValid() {
        BoundingBox bbox = BoundingBox.parse("50.9719467,35.5821,51.6347084,35.8002701");

        assertThat(bbox.west()).isEqualTo(50.9719467);
        assertThat(bbox.south()).isEqualTo(35.5821);
        assertThat(bbox.east()).isEqualTo(51.6347084);
        assertThat(bbox.north()).isEqualTo(35.8002701);
    }

    @Test
    @DisplayName("It should round-trip through toCsv.")
    void should_RoundTrip_When_ConvertedToCsv() {
        BoundingBox bbox = BoundingBox.of(50.0, 35.0, 51.0, 36.0);

        assertThat(BoundingBox.parse(bbox.toCsv())).isEqualTo(bbox);
    }

    @Test
    @DisplayName("It should reject csv without exactly four components.")
    void should_Throw_When_CsvHasWrongArity() {
        assertThatThrownBy(() -> BoundingBox.parse("50.0,35.0,51.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bbox must be 'west,south,east,north'");
    }

    @Test
    @DisplayName("It should reject blank csv.")
    void should_Throw_When_CsvBlank() {
        assertThatThrownBy(() -> BoundingBox.parse("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bbox is required");

        assertThatThrownBy(() -> BoundingBox.parse(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bbox is required");
    }

    @Test
    @DisplayName("It should reject csv with non-numeric components.")
    void should_Throw_When_CsvNotNumeric() {
        assertThatThrownBy(() -> BoundingBox.parse("a,b,c,d"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bbox components must be numbers");
    }

    @Test
    @DisplayName("It should reject west greater than or equal to east.")
    void should_Throw_When_WestNotLessThanEast() {
        assertThatThrownBy(() -> BoundingBox.of(51.0, 35.0, 50.0, 36.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("west must be less than east");
    }

    @Test
    @DisplayName("It should reject south greater than or equal to north.")
    void should_Throw_When_SouthNotLessThanNorth() {
        assertThatThrownBy(() -> BoundingBox.of(50.0, 36.0, 51.0, 35.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("south must be less than north");
    }

    @Test
    @DisplayName("It should reject longitude values outside the valid range.")
    void should_Throw_When_LongitudeOutOfRange() {
        assertThatThrownBy(() -> BoundingBox.of(-181.0, 35.0, 50.0, 36.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("west must be between -180 and 180");
    }

    @Test
    @DisplayName("It should reject latitude values outside the valid range.")
    void should_Throw_When_LatitudeOutOfRange() {
        assertThatThrownBy(() -> BoundingBox.of(50.0, -91.0, 51.0, 36.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("south must be between -90 and 90");
    }

    @Test
    @DisplayName("It should return null when all ofNullable components are null and reject partial input.")
    void should_HandleNullComponents_When_UsingOfNullable() {
        assertThat(BoundingBox.ofNullable(null, null, null, null)).isNull();
        assertThatThrownBy(() -> BoundingBox.ofNullable(50.0, null, 51.0, 36.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("It should contain points inside the box and reject points outside.")
    void should_DetectContainment_When_CheckingPoints() {
        BoundingBox bbox = BoundingBox.of(50.0, 35.0, 51.0, 36.0);

        assertThat(bbox.contains(GeoPoint.of(35.5, 50.5))).isTrue();
        assertThat(bbox.contains(GeoPoint.of(34.0, 50.5))).isFalse();
        assertThat(bbox.contains(GeoPoint.of(35.5, 52.0))).isFalse();
        assertThat(bbox.contains(null)).isFalse();
    }
}
