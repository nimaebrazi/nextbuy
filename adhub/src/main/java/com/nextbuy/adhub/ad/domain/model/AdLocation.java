package com.nextbuy.adhub.ad.domain.model;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Where an ad is located — immutable value object owned by the Ad aggregate.
 * Uses opaque UUID references validated via {@code location.api}.
 */
public final class AdLocation {

    private final UUID countryId;
    private final UUID provinceId;
    private final UUID cityId;
    private final UUID neighbourhoodId;
    private final Double latitude;
    private final Double longitude;

    private AdLocation(
            UUID countryId,
            UUID provinceId,
            UUID cityId,
            UUID neighbourhoodId,
            Double latitude,
            Double longitude
    ) {
        Objects.requireNonNull(countryId, "countryId is required");
        Objects.requireNonNull(provinceId, "provinceId is required");
        Objects.requireNonNull(cityId, "cityId is required");
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("latitude and longitude must be provided together or omitted");
        }
        this.countryId = countryId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.neighbourhoodId = neighbourhoodId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static AdLocation of(
            UUID countryId,
            UUID provinceId,
            UUID cityId,
            UUID neighbourhoodId,
            Double latitude,
            Double longitude
    ) {
        return new AdLocation(countryId, provinceId, cityId, neighbourhoodId, latitude, longitude);
    }

    public UUID countryId() {
        return countryId;
    }

    public UUID provinceId() {
        return provinceId;
    }

    public UUID cityId() {
        return cityId;
    }

    public Optional<UUID> neighbourhoodId() {
        return Optional.ofNullable(neighbourhoodId);
    }

    public Optional<Double> latitude() {
        return Optional.ofNullable(latitude);
    }

    public Optional<Double> longitude() {
        return Optional.ofNullable(longitude);
    }

    public boolean hasExactLocation() {
        return latitude != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdLocation that = (AdLocation) o;
        return countryId.equals(that.countryId)
                && provinceId.equals(that.provinceId)
                && cityId.equals(that.cityId)
                && Objects.equals(neighbourhoodId, that.neighbourhoodId)
                && Objects.equals(latitude, that.latitude)
                && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId, provinceId, cityId, neighbourhoodId, latitude, longitude);
    }

    @Override
    public String toString() {
        return "AdLocation[country=" + countryId + ", province=" + provinceId
                + ", city=" + cityId + ", neighbourhood=" + neighbourhoodId
                + ", lat=" + latitude + ", lon=" + longitude + "]";
    }
}
