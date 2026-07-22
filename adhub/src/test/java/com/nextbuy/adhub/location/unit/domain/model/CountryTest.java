package com.nextbuy.adhub.location.unit.domain.model;

import com.nextbuy.adhub.location.domain.exception.LocationDomainException;
import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.Country;
import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.domain.model.GeoBoundary;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import com.nextbuy.adhub.support.location.fixtures.Countries;
import com.nextbuy.adhub.support.location.fixtures.Geos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static com.nextbuy.adhub.support.location.fixtures.Countries.TEST_COUNTRY_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:CountryTest")
class CountryTest {

    @Test
    @DisplayName("It should normalize slug and iso code when a country is created.")
    void should_NormalizeFields_When_Created() {
        Country country = Countries.withFields(" Iran ", " ir ", "  IRAN  ");

        assertThat(country.getName()).isEqualTo("Iran");
        assertThat(country.getIsoCode()).isEqualTo("IR");
        assertThat(country.getSlug()).isEqualTo("iran");
        assertThat(country.getId().isAssigned()).isFalse();
        assertThat(country.getBbox()).isNull();
    }

    @Test
    @DisplayName("It should reject creation when the iso code is invalid.")
    void should_Throw_When_IsoCodeInvalid() {
        assertThatThrownBy(() -> Countries.withIsoCode("12"))
                .isInstanceOf(LocationDomainException.CountryMustBeIsoCode.class);

        assertThatThrownBy(() -> Countries.withIsoCode("IRN"))
                .isInstanceOf(LocationDomainException.CountryMustBeIsoCode.class);
    }

    @Test
    @DisplayName("It should reject creation when the name is missing.")
    void should_Throw_When_NameMissing() {
        assertThatThrownBy(() -> Countries.withName("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should reject creation when the slug is invalid.")
    void should_Throw_When_SlugInvalid() {
        assertThatThrownBy(() -> Countries.withSlug("ir an!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class);
    }

    @Test
    @DisplayName("It should rehydrate a persisted country without re-validating.")
    void should_Rehydrate_When_ReconstructCalled() {
        BoundingBox bbox = BoundingBox.of(50.0, 35.0, 52.0, 36.0);
        Country country = Countries.reconstructed(TEST_COUNTRY_ID, bbox);

        assertThat(country.getId()).isEqualTo(CountryId.of(TEST_COUNTRY_ID));
        assertThat(country.getBbox()).isEqualTo(bbox);
    }

    @Test
    @DisplayName("It should trim the name when the country is renamed.")
    void should_TrimName_When_Renamed() {
        Country country = Countries.iran();

        country.rename("  Persia  ");

        assertThat(country.getName()).isEqualTo("Persia");
    }

    @Test
    @DisplayName("It should reject rename when the name is blank.")
    void should_Throw_When_RenameNameBlank() {
        Country country = Countries.iran();

        assertThatThrownBy(() -> country.rename("  "))
                .isInstanceOf(LocationDomainException.FieldIsRequired.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("It should normalize the slug when changeSlug is called.")
    void should_NormalizeSlug_When_SlugChanged() {
        Country country = Countries.iran();

        country.changeSlug("  PERSIA  ");

        assertThat(country.getSlug()).isEqualTo("persia");
    }

    @Test
    @DisplayName("It should reject changeSlug when the slug is invalid.")
    void should_Throw_When_ChangeSlugInvalid() {
        Country country = Countries.iran();

        assertThatThrownBy(() -> country.changeSlug("bad slug!"))
                .isInstanceOf(LocationDomainException.InvalidSlug.class);
    }

    @Test
    @DisplayName("It should store centroid and boundary when they are updated.")
    void should_StoreGeo_When_CentroidAndBoundaryUpdated() {
        Country country = Countries.iran();
        GeoPoint centroid = GeoPoint.of(32.0, 53.0);
        GeoBoundary boundary = Geos.squareBoundary(32.0, 53.0, 1.0);

        country.updateCentroid(centroid);
        country.updateBoundary(boundary);

        assertThat(country.getCentroid()).isEqualTo(centroid);
        assertThat(country.getBoundary()).isEqualTo(boundary);

        country.updateCentroid(null);
        country.updateBoundary(null);

        assertThat(country.getCentroid()).isNull();
        assertThat(country.getBoundary()).isNull();
    }
}
