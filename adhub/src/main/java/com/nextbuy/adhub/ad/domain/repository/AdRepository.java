package com.nextbuy.adhub.ad.domain.repository;

import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;

import java.util.Optional;

public interface AdRepository {
    /**
     * Inserts a new ad (unassigned id). Returns the DB-generated id.
     * Does not mutate the aggregate — caller must invoke {@code ad.assignId(id)}.
     */
    AdId saveNew(Ad ad);

    Ad save(Ad ad);

    Optional<Ad> findById(AdId id);
}
