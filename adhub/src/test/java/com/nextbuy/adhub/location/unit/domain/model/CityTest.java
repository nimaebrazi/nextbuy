package com.nextbuy.adhub.location.unit.domain.model;

import com.nextbuy.adhub.location.domain.exception.LocationDomainException;
import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.City;
import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.GeoBoundary;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import com.nextbuy.adhub.location.domain.model.ProvinceId;
import com.nextbuy.adhub.support.location.fixtures.Cities;
import com.nextbuy.adhub.support.location.fixtures.Geos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:CityTest")
class CityTest {

    @Test
    @DisplayName("It should normalize the slug when a city is created.")
    void should_NormalizeSlug_When_Created() {
        City city = Cities.withNameAndSlug(" Tehran ", "  TEHRAN  ");

        assertThat(city.getName()).isEqualTo("Tehran");
        assertThat(city.getSlug()).isEqualTo("tehran");
        assertThat(city.getId().isAssigned()).isFalse();
        assertThat(city.getBbox()).isNull();
    }

    @Test
    @DisplayName("It should reject creation when the name is missing.")
    void should_Throw_When_NameMissing() {
        assertThatThrownBy(() -> Cities.withName("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should reject creation when the province is missing.")
    void should_Throw_When_ProvinceMissing() {
        assertThatThrownBy(Cities::withoutProvince)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("provinceId is required");
    }

    @Test
    @DisplayName("It should reject creation when the province id is unassigned.")
    void should_Throw_When_ProvinceUnassigned() {
        assertThatThrownBy(() -> Cities.withProvince(ProvinceId.unassigned()))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("provinceId");
    }

    @Test
    @DisplayName("It should reject creation when the slug is invalid.")
    void should_Throw_When_SlugInvalid() {
        assertThatThrownBy(() -> Cities.withNameAndSlug("Tehran", "teh ran!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class);
    }

    @Test
    @DisplayName("It should rehydrate a persisted city without re-validating.")
    void should_Rehydrate_When_ReconstructCalled() {
        BoundingBox bbox = BoundingBox.of(50.9719467, 35.5821, 51.6347084, 35.8002701);
        var cityId = UUID.randomUUID();
        City city = Cities.reconstructed(cityId, bbox);

        assertThat(city.getId()).isEqualTo(CityId.of(cityId));
        assertThat(city.getBbox()).isEqualTo(bbox);
    }

    @Test
    @DisplayName("It should trim the name when the city is renamed.")
    void should_TrimName_When_Renamed() {
        City city = Cities.withNameAndSlug("Tehran", "tehran");

        city.rename("  Karaj  ");

        assertThat(city.getName()).isEqualTo("Karaj");
    }

    @Test
    @DisplayName("It should reject rename when the name is blank.")
    void should_Throw_When_RenameNameBlank() {
        City city = Cities.withNameAndSlug("Tehran", "tehran");

        assertThatThrownBy(() -> city.rename("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should normalize the slug when changeSlug is called.")
    void should_NormalizeSlug_When_SlugChanged() {
        City city = Cities.withNameAndSlug("Tehran", "tehran");

        city.changeSlug("  KARAJ  ");

        assertThat(city.getSlug()).isEqualTo("karaj");
    }

    @Test
    @DisplayName("It should reject changeSlug when the slug is invalid.")
    void should_Throw_When_ChangeSlugInvalid() {
        City city = Cities.withNameAndSlug("Tehran", "tehran");

        assertThatThrownBy(() -> city.changeSlug("bad slug!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class);
    }

    @Test
    @DisplayName("It should store centroid and boundary when they are updated.")
    void should_StoreGeo_When_CentroidAndBoundaryUpdated() {
        City city = Cities.withNameAndSlug("Tehran", "tehran");
        GeoPoint centroid = GeoPoint.of(35.6892, 51.3890);
        GeoBoundary boundary = Geos.squareBoundary(35.6892, 51.3890, 0.2);

        city.updateCentroid(centroid);
        city.updateBoundary(boundary);

        assertThat(city.getCentroid()).isEqualTo(centroid);
        assertThat(city.getBoundary()).isEqualTo(boundary);

        city.updateCentroid(null);
        city.updateBoundary(null);

        assertThat(city.getCentroid()).isNull();
        assertThat(city.getBoundary()).isNull();
    }
}
