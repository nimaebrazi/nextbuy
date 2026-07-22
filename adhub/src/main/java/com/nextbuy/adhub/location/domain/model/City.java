package com.nextbuy.adhub.location.domain.model;

import java.util.Objects;

import com.nextbuy.adhub.location.domain.SlugValidator;
import com.nextbuy.adhub.location.domain.exception.LocationDomainException;

/**
 * City — third level of the location hierarchy, belongs to a {@link Province}.
 *
 * <p>Slug uniqueness is enforced at the infrastructure layer (DB constraint + repository).</p>
 */
public class City {

    private final CityId id;
    private final ProvinceId provinceId;
    private final BoundingBox bbox; // nullable; trigger-derived, read-only

    private String name;
    private String slug;
    private GeoPoint centroid;    // nullable
    private GeoBoundary boundary; // nullable

    public static City create(
            String name,
            String slug,
            ProvinceId provinceId,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
        var city = new City(
                CityId.unassigned(),
                name == null ? null : name.trim(),
                slug,
                provinceId,
                centroid,
                boundary,
                null
        );
        city.validateCity();
        city.initializeCity();
        return city;
    }

    public static City reconstruct(
            CityId id,
            String name,
            String slug,
            ProvinceId provinceId,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        return new City(id, name, slug, provinceId, centroid, boundary, bbox);
    }

    private City(
            CityId id,
            String name,
            String slug,
            ProvinceId provinceId,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.provinceId = provinceId;
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

    private void validateCity() {
        validateName(name);
        validateProvinceId(provinceId);
    }

    private void initializeCity() {
        slug = SlugValidator.validateAndNormalize(slug);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new LocationDomainException.FieldIsRequired("City", "name");
        }
    }

    private static void validateProvinceId(ProvinceId provinceId) {
        Objects.requireNonNull(provinceId, "provinceId is required");
        if (!provinceId.isAssigned()) {
            throw new LocationDomainException.FieldIsRequired("City", "provinceId");
        }
    }

    public CityId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public ProvinceId getProvinceId() {
        return provinceId;
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
