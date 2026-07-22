package com.nextbuy.adhub.location.domain.model;

import java.util.UUID;

import com.nextbuy.adhub.shared.domain.BaseId;

public class CountryId extends BaseId<UUID> {

    private CountryId(UUID id) {
        super(id);
    }

    public static CountryId of(UUID id) {
        return new CountryId(id);
    }

    public static CountryId unassigned() {
        return new CountryId(null);
    }
}
