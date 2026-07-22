package com.nextbuy.adhub.ad.application.command.create;

import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;
import java.util.UUID;

public record AdCreateCommand(
        long ownerId,
        String title,
        String description,
        BigDecimal price,
        String currency,
        UUID categoryId,
        UUID countryId,
        UUID provinceId,
        UUID cityId,
        UUID neighbourhoodId,
        Double latitude,
        Double longitude
) {
    @AssertTrue(message = "latitude and longitude must be provided together or omitted together")
    public boolean isCoordinatePairComplete() {
        return (latitude == null) == (longitude == null);
    }
}
