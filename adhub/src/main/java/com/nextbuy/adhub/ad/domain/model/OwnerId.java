package com.nextbuy.adhub.ad.domain.model;

import com.nextbuy.adhub.shared.domain.BaseId;

public final class OwnerId extends BaseId<Long> {
    private OwnerId(Long id) {
        super(id);
    }

    public static OwnerId of(long id) {
        return new OwnerId(id);
    }

    public static OwnerId unassigned() {
        return new OwnerId(null);
    }
}
