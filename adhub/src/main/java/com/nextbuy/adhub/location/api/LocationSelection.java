package com.nextbuy.adhub.location.api;

import java.util.UUID;

public record LocationSelection(
        UUID countryId,
        UUID provinceId,
        UUID cityId,
        UUID neighbourhoodId,
        Double latitude,
        Double longitude
) {
}
