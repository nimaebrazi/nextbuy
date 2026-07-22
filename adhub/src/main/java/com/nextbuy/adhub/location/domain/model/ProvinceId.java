package com.nextbuy.adhub.location.domain.model;

import java.util.UUID;

import com.nextbuy.adhub.shared.domain.BaseId;

public class ProvinceId extends BaseId<UUID> {

    private ProvinceId(UUID id) {
        super(id);
    }

    public static ProvinceId of(UUID id) {
        return new ProvinceId(id);
    }

    public static ProvinceId unassigned() {
        return new ProvinceId(null);
    }
}
