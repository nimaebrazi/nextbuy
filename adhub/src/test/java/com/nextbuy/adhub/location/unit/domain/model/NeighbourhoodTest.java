package com.nextbuy.adhub.location.unit.domain.model;

import com.nextbuy.adhub.location.domain.exception.LocationDomainException;
import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import com.nextbuy.adhub.location.domain.model.Neighbourhood;
import com.nextbuy.adhub.location.domain.model.NeighbourhoodId;
import com.nextbuy.adhub.support.location.fixtures.Geos;
import com.nextbuy.adhub.support.location.fixtures.Neighbourhoods;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static com.nextbuy.adhub.support.location.fixtures.Neighbourhoods.TEST_CITY_ID;
import static com.nextbuy.adhub.support.location.fixtures.Neighbourhoods.TEST_NEIGHBOURHOOD_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:NeighbourhoodTest")
class NeighbourhoodTest {

    @Test
    @DisplayName("It should return true when a point is inside the neighbourhood boundary.")
    void should_Contain_When_PointInsideBoundary() {
        var boundary = Geos.squareBoundary(35.7575, 51.4100, 0.01);
        Neighbourhood vanak = Neighbourhoods.withIdCityAndBoundary(
                TEST_NEIGHBOURHOOD_ID, CityId.of(TEST_CITY_ID), boundary);

        assertThat(vanak.contains(GeoPoint.of(35.7575, 51.4100))).isTrue();
        assertThat(vanak.contains(GeoPoint.of(35.80, 51.50))).isFalse();
    }

    @Test
    @DisplayName("It should return false when the neighbourhood has no boundary.")
    void should_NotContain_When_BoundaryMissing() {
        Neighbourhood withoutBoundary = Neighbourhoods.withId(TEST_NEIGHBOURHOOD_ID);

        assertThat(withoutBoundary.contains(GeoPoint.of(35.7575, 51.4100))).isFalse();
    }

    @Test
    @DisplayName("It should return false when the point is null.")
    void should_NotContain_When_PointIsNull() {
        var boundary = Geos.squareBoundary(35.7575, 51.4100, 0.01);
        Neighbourhood neighbourhood = Neighbourhoods.withIdCityAndBoundary(
                TEST_NEIGHBOURHOOD_ID, CityId.of(TEST_CITY_ID), boundary);

        assertThat(neighbourhood.contains(null)).isFalse();
    }

    @Test
    @DisplayName("It should normalize the slug when a neighbourhood is created.")
    void should_NormalizeSlug_When_Created() {
        Neighbourhood neighbourhood = Neighbourhoods.withNameAndSlug("Vanak", "  VANAK  ");

        assertThat(neighbourhood.getSlug()).isEqualTo("vanak");
    }

    @Test
    @DisplayName("It should reject creation when the name is blank.")
    void should_Throw_When_NameMissing() {
        assertThatThrownBy(() -> Neighbourhoods.withName("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should reject creation when the slug is invalid.")
    void should_Throw_When_SlugInvalid() {
        assertThatThrownBy(() -> Neighbourhoods.withNameAndSlug("Vanak", "van ak!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class)
                .hasMessageContaining("slug");
    }

    @Test
    @DisplayName("It should reject creation when the city is missing.")
    void should_Throw_When_CityMissing() {
        assertThatThrownBy(Neighbourhoods::withoutCity)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("cityId is required");
    }

    @Test
    @DisplayName("It should reject creation when the city id is unassigned.")
    void should_Throw_When_CityUnassigned() {
        assertThatThrownBy(() -> Neighbourhoods.withCity(CityId.unassigned()))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("cityId");
    }

    @Test
    @DisplayName("It should rehydrate a persisted neighbourhood without re-validating.")
    void should_Rehydrate_When_ReconstructCalled() {
        BoundingBox bbox = BoundingBox.of(51.4, 35.75, 51.42, 35.76);
        Neighbourhood neighbourhood = Neighbourhoods.reconstructed(TEST_NEIGHBOURHOOD_ID, bbox);

        assertThat(neighbourhood.getId()).isEqualTo(NeighbourhoodId.of(TEST_NEIGHBOURHOOD_ID));
        assertThat(neighbourhood.getBbox()).isEqualTo(bbox);
    }

    @Test
    @DisplayName("It should trim the name when the neighbourhood is renamed.")
    void should_TrimName_When_Renamed() {
        Neighbourhood neighbourhood = Neighbourhoods.withNameAndSlug("Vanak", "vanak");

        neighbourhood.rename("  Jordan  ");

        assertThat(neighbourhood.getName()).isEqualTo("Jordan");
    }

    @Test
    @DisplayName("It should reject rename when the name is blank.")
    void should_Throw_When_RenameNameBlank() {
        Neighbourhood neighbourhood = Neighbourhoods.withNameAndSlug("Vanak", "vanak");

        assertThatThrownBy(() -> neighbourhood.rename("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should normalize the slug when changeSlug is called.")
    void should_NormalizeSlug_When_SlugChanged() {
        Neighbourhood neighbourhood = Neighbourhoods.withNameAndSlug("Vanak", "vanak");

        neighbourhood.changeSlug("  JORDAN  ");

        assertThat(neighbourhood.getSlug()).isEqualTo("jordan");
    }

    @Test
    @DisplayName("It should reject changeSlug when the slug is invalid.")
    void should_Throw_When_ChangeSlugInvalid() {
        Neighbourhood neighbourhood = Neighbourhoods.withNameAndSlug("Vanak", "vanak");

        assertThatThrownBy(() -> neighbourhood.changeSlug("bad slug!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class);
    }

    @Test
    @DisplayName("It should store centroid and boundary when they are updated.")
    void should_StoreGeo_When_CentroidAndBoundaryUpdated() {
        Neighbourhood neighbourhood = Neighbourhoods.withNameAndSlug("Vanak", "vanak");
        GeoPoint centroid = GeoPoint.of(35.7575, 51.4100);
        var boundary = Geos.squareBoundary(35.7575, 51.4100, 0.01);

        neighbourhood.updateCentroid(centroid);
        neighbourhood.updateBoundary(boundary);

        assertThat(neighbourhood.getCentroid()).isEqualTo(centroid);
        assertThat(neighbourhood.getBoundary()).isEqualTo(boundary);

        neighbourhood.updateCentroid(null);
        neighbourhood.updateBoundary(null);

        assertThat(neighbourhood.getCentroid()).isNull();
        assertThat(neighbourhood.getBoundary()).isNull();
    }
}
