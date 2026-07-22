package com.nextbuy.adhub.location.domain.model;

import java.util.UUID;

import com.nextbuy.adhub.shared.domain.BaseId;

public class NeighbourhoodId extends BaseId<UUID> {

    private NeighbourhoodId(UUID id) {
        super(id);
    }

    public static NeighbourhoodId of(UUID id) {
        return new NeighbourhoodId(id);
    }

    public static NeighbourhoodId unassigned() {
        return new NeighbourhoodId(null);
    }
}
