package com.nextbuy.adhub.ad.domain.model;

import java.util.UUID;

import com.nextbuy.adhub.shared.domain.BaseId;


public final class CategoryId extends BaseId<UUID> {
    private CategoryId(UUID id) {
        super(id);
    }

    public static CategoryId of(UUID id) {
        return new CategoryId(id);
    }

    public static CategoryId unassigned() {
        return new CategoryId(null);
    }

}
