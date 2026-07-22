package com.nextbuy.adhub.location.unit.domain.model;

import com.nextbuy.adhub.location.domain.exception.LocationDomainException;
import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.domain.model.GeoBoundary;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import com.nextbuy.adhub.location.domain.model.Province;
import com.nextbuy.adhub.location.domain.model.ProvinceId;
import com.nextbuy.adhub.support.location.fixtures.Geos;
import com.nextbuy.adhub.support.location.fixtures.Provinces;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static com.nextbuy.adhub.support.location.fixtures.Provinces.TEST_PROVINCE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:ProvinceTest")
class ProvinceTest {

    @Test
    @DisplayName("It should normalize the slug when a province is created.")
    void should_NormalizeSlug_When_Created() {
        Province province = Provinces.withNameAndSlug(" Tehran ", "  TEHRAN  ");

        assertThat(province.getName()).isEqualTo("Tehran");
        assertThat(province.getSlug()).isEqualTo("tehran");
        assertThat(province.getId().isAssigned()).isFalse();
        assertThat(province.getBbox()).isNull();
    }

    @Test
    @DisplayName("It should reject creation when the name is missing.")
    void should_Throw_When_NameMissing() {
        assertThatThrownBy(() -> Provinces.withName("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should reject creation when the country is missing.")
    void should_Throw_When_CountryMissing() {
        assertThatThrownBy(Provinces::withoutCountry)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("countryId is required");
    }

    @Test
    @DisplayName("It should reject creation when the country id is unassigned.")
    void should_Throw_When_CountryUnassigned() {
        assertThatThrownBy(() -> Provinces.withCountry(CountryId.unassigned()))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("countryId");
    }

    @Test
    @DisplayName("It should reject creation when the slug is invalid.")
    void should_Throw_When_SlugInvalid() {
        assertThatThrownBy(() -> Provinces.withNameAndSlug("Tehran", "teh ran!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class);
    }

    @Test
    @DisplayName("It should rehydrate a persisted province without re-validating.")
    void should_Rehydrate_When_ReconstructCalled() {
        BoundingBox bbox = BoundingBox.of(50.0, 35.0, 52.0, 36.0);
        Province province = Provinces.reconstructed(TEST_PROVINCE_ID, bbox);

        assertThat(province.getId()).isEqualTo(ProvinceId.of(TEST_PROVINCE_ID));
        assertThat(province.getBbox()).isEqualTo(bbox);
    }

    @Test
    @DisplayName("It should trim the name when the province is renamed.")
    void should_TrimName_When_Renamed() {
        Province province = Provinces.withNameAndSlug("Tehran", "tehran");

        province.rename("  Alborz  ");

        assertThat(province.getName()).isEqualTo("Alborz");
    }

    @Test
    @DisplayName("It should reject rename when the name is blank.")
    void should_Throw_When_RenameNameBlank() {
        Province province = Provinces.withNameAndSlug("Tehran", "tehran");

        assertThatThrownBy(() -> province.rename("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should normalize the slug when changeSlug is called.")
    void should_NormalizeSlug_When_SlugChanged() {
        Province province = Provinces.withNameAndSlug("Tehran", "tehran");

        province.changeSlug("  ALBORZ  ");

        assertThat(province.getSlug()).isEqualTo("alborz");
    }

    @Test
    @DisplayName("It should reject changeSlug when the slug is invalid.")
    void should_Throw_When_ChangeSlugInvalid() {
        Province province = Provinces.withNameAndSlug("Tehran", "tehran");

        assertThatThrownBy(() -> province.changeSlug("bad slug!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class);
    }

    @Test
    @DisplayName("It should store centroid and boundary when they are updated.")
    void should_StoreGeo_When_CentroidAndBoundaryUpdated() {
        Province province = Provinces.withNameAndSlug("Tehran", "tehran");
        GeoPoint centroid = GeoPoint.of(35.7, 51.4);
        GeoBoundary boundary = Geos.squareBoundary(35.7, 51.4, 0.5);

        province.updateCentroid(centroid);
        province.updateBoundary(boundary);

        assertThat(province.getCentroid()).isEqualTo(centroid);
        assertThat(province.getBoundary()).isEqualTo(boundary);

        province.updateCentroid(null);
        province.updateBoundary(null);

        assertThat(province.getCentroid()).isNull();
        assertThat(province.getBoundary()).isNull();
    }
}
