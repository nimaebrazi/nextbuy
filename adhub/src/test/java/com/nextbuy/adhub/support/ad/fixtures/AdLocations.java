package com.nextbuy.adhub.support.ad.fixtures;

import com.nextbuy.adhub.ad.domain.model.AdLocation;

import java.util.UUID;

public final class AdLocations {

    private AdLocations() {
    }

    public static AdLocation fixtureIds() {
        return AdLocation.of(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                null, null, null
        );
    }

    public static AdLocation random() {
        return AdLocation.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                null,
                null
        );
    }
}
