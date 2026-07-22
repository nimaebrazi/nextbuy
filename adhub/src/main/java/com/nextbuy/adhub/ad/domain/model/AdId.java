package com.nextbuy.adhub.ad.domain.model;

import java.util.UUID;

import com.nextbuy.adhub.shared.domain.BaseId;

public final class AdId extends BaseId<UUID> {
    private AdId(UUID id) {
        super(id);
    }

    public static AdId of(UUID id) {
        return new AdId(id);
    }

    public static AdId unassigned() {
        return new AdId(null);
    }
}
