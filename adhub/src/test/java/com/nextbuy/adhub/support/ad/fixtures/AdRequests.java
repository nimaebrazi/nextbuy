package com.nextbuy.adhub.support.ad.fixtures;

import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdRequest;

import java.math.BigDecimal;
import java.util.UUID;

public class AdRequests {
    private AdRequests() {
    }

    public final static UUID CATEGORY_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    public final static UUID COUNTRY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public final static UUID PROVINCE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public final static UUID CITY_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public final static UUID NEIGHBOURHOOD_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    ;

    public static CreateAdRequest randomCreateAdRequest() {
        return new CreateAdRequest(
                "S26 Ultra",
                "Samsung android device",
                new BigDecimal(3000),
                "USD",
                CATEGORY_ID,
                COUNTRY_ID,
                PROVINCE_ID,
                CITY_ID,
                NEIGHBOURHOOD_ID,
                35.7447636,
                51.3729263
        );
    }
}
