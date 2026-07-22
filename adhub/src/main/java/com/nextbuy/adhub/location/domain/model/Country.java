package com.nextbuy.adhub.location.domain.model;

import java.util.Locale;

import com.nextbuy.adhub.location.domain.SlugValidator;
import com.nextbuy.adhub.location.domain.exception.LocationDomainException;

/**
 * Reference-data root of the location hierarchy (Country → Province → City → Neighbourhood).
 *
 * <p>{@code bbox} is DB-derived (trigger computes it from {@code boundary}), so the domain
 * treats it as read-only: only set when reconstructing from persistence, no mutator —
 * updating {@code boundary} refreshes it on the next load.</p>
 */
public class Country {

    private final CountryId id;
    private final BoundingBox bbox; // nullable; trigger-derived, read-only

    private String name;
    private String isoCode;       // 2-letter, uppercase
    private String slug;
    private GeoPoint centroid;    // nullable
    private GeoBoundary boundary; // nullable

    public static Country create(
            String name,
            String isoCode,
            String slug,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
        var country = new Country(
                CountryId.unassigned(),
                name == null ? null : name.trim(),
                isoCode == null ? null : isoCode.trim(),
                slug,
                centroid,
                boundary,
                null
        );
        country.validateCountry();
        country.initializeCountry();
        return country;
    }

    public static Country reconstruct(
            CountryId id,
            String name,
            String isoCode,
            String slug,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        return new Country(id, name, isoCode, slug, centroid, boundary, bbox);
    }

    private Country(
            CountryId id,
            String name,
            String isoCode,
            String slug,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        this.id = id;
        this.name = name;
        this.isoCode = isoCode;
        this.slug = slug;
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

    private void validateCountry() {
        validateName(name);
        validateIsoCode(isoCode);
    }

    private void initializeCountry() {
        isoCode = isoCode.toUpperCase(Locale.ROOT);
        slug = SlugValidator.validateAndNormalize(slug);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new LocationDomainException.FieldIsRequired("Country", "name");
        }
    }

    private static void validateIsoCode(String isoCode) {
        if (isoCode == null || isoCode.length() != 2 || !isoCode.chars().allMatch(Character::isLetter)) {
            throw new LocationDomainException.CountryMustBeIsoCode();
        }
    }

    public CountryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getSlug() {
        return slug;
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
