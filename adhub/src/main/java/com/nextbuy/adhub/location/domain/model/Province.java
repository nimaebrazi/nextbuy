package com.nextbuy.adhub.location.domain.model;

import java.util.Objects;

import com.nextbuy.adhub.location.domain.SlugValidator;
import com.nextbuy.adhub.location.domain.exception.LocationDomainException;

/**
 * Province — second level of the location hierarchy, belongs to a {@link Country}.
 */
public class Province {

    private final ProvinceId id;
    private final CountryId countryId;
    private final BoundingBox bbox; // nullable; trigger-derived, read-only

    private String name;
    private String slug;
    private GeoPoint centroid;    // nullable
    private GeoBoundary boundary; // nullable

    public static Province create(
            String name,
            String slug,
            CountryId countryId,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
        var province = new Province(
                ProvinceId.unassigned(),
                name == null ? null : name.trim(),
                slug,
                countryId,
                centroid,
                boundary,
                null
        );
        province.validateProvince();
        province.initializeProvince();
        return province;
    }

    public static Province reconstruct(
            ProvinceId id,
            String name,
            String slug,
            CountryId countryId,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        return new Province(id, name, slug, countryId, centroid, boundary, bbox);
    }

    private Province(
            ProvinceId id,
            String name,
            String slug,
            CountryId countryId,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.countryId = countryId;
        this.centroid = centroid;
        this.boundary = boundary;
        this.bbox = bbox;
    }

    public void rename(String newName) {
        String trimmedName = newName == null ? null : newName.trim();
        validateName(trimmedName);
        this.name = trimmedName;
    }

    public void changeSlug(String newSlug) {
        this.slug = SlugValidator.validateAndNormalize(newSlug);
    }

    public void updateCentroid(GeoPoint centroid) {
        this.centroid = centroid;
    }

    public void updateBoundary(GeoBoundary boundary) {
        this.boundary = boundary;
    }

    private void validateProvince() {
        validateName(name);
        validateCountryId(countryId);
    }

    private void initializeProvince() {
        slug = SlugValidator.validateAndNormalize(slug);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new LocationDomainException.FieldIsRequired("Province", "name");
        }
    }

    private static void validateCountryId(CountryId countryId) {
        Objects.requireNonNull(countryId, "countryId is required");
        if (!countryId.isAssigned()) {
            throw new LocationDomainException.FieldIsRequired("Province", "countryId");
        }
    }

    public ProvinceId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public CountryId getCountryId() {
        return countryId;
    }

    public GeoPoint getCentroid() {
        return centroid;
    }

    public GeoBoundary getBoundary() {
        return boundary;
    }

    public BoundingBox getBbox() {
        return bbox;
    }
}
