package com.nextbuy.adhub.location.domain.model;

import java.util.Objects;

import com.nextbuy.adhub.location.domain.SlugValidator;
import com.nextbuy.adhub.location.domain.exception.LocationDomainException;

/**
 * Neighbourhood — leaf level of the location hierarchy, belongs to a {@link City}.
 */
public class Neighbourhood {

    private final NeighbourhoodId id;
    private final CityId cityId;
    private final BoundingBox bbox; // nullable; trigger-derived, read-only

    private String name;
    private String slug;
    private GeoPoint centroid;    // nullable
    private GeoBoundary boundary; // nullable

    public static Neighbourhood create(
            String name,
            String slug,
            CityId cityId,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
        var neighbourhood = new Neighbourhood(
                NeighbourhoodId.unassigned(),
                name == null ? null : name.trim(),
                slug,
                cityId,
                centroid,
                boundary,
                null
        );
        neighbourhood.validateNeighbourhood();
        neighbourhood.initializeNeighbourhood();
        return neighbourhood;
    }

    public static Neighbourhood reconstruct(
            NeighbourhoodId id,
            String name,
            String slug,
            CityId cityId,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        return new Neighbourhood(id, name, slug, cityId, centroid, boundary, bbox);
    }

    private Neighbourhood(
            NeighbourhoodId id,
            String name,
            String slug,
            CityId cityId,
            GeoPoint centroid,
            GeoBoundary boundary,
            BoundingBox bbox
    ) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.cityId = cityId;
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

    /**
     * True when the point is inside this neighbourhood's boundary polygon.
     * False when no boundary is seeded.
     */
    public boolean contains(GeoPoint point) {
        return boundary != null && boundary.contains(point);
    }

    private void validateNeighbourhood() {
        validateName(name);
        validateCityId(cityId);
    }

    private void initializeNeighbourhood() {
        slug = SlugValidator.validateAndNormalize(slug);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new LocationDomainException.FieldIsRequired("Neighbourhood", "name");
        }
    }

    private static void validateCityId(CityId cityId) {
        Objects.requireNonNull(cityId, "cityId is required");
        if (!cityId.isAssigned()) {
            throw new LocationDomainException.FieldIsRequired("Neighbourhood", "cityId");
        }
    }

    public NeighbourhoodId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public CityId getCityId() {
        return cityId;
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
