package com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAdRequest(
        @NotBlank @Size(max = 200)
        String title,

        @NotBlank
        String description,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal priceAmount,

        @NotBlank
        @Size(min = 3, max = 3)
        String priceCurrency,

        @NotNull UUID categoryId,

        @NotNull UUID countryId,

        @NotNull UUID provinceId,

        @NotNull UUID cityId,

        UUID neighbourhoodId,

        Double latitude,
        Double longitude
) {

    @AssertTrue(message = "latitude and longitude must be provided together or omitted together")
    public boolean isCoordinatePairComplete() {
        return (latitude == null) == (longitude == null);
    }
}
