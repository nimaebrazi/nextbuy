package com.nextbuy.adhub.location.domain.model;

import java.util.UUID;

import com.nextbuy.adhub.shared.domain.BaseId;

public class CityId extends BaseId<UUID> {

    private CityId(UUID id) {
        super(id);
    }

    public static CityId of(UUID id) {
        return new CityId(id);
    }

    public static CityId unassigned() {
        return new CityId(null);
    }
}
