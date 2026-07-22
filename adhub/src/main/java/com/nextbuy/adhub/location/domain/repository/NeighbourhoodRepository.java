package com.nextbuy.adhub.location.domain.repository;

import java.util.List;
import java.util.Optional;

import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.Neighbourhood;
import com.nextbuy.adhub.location.domain.model.NeighbourhoodId;

public interface NeighbourhoodRepository {

    Neighbourhood save(Neighbourhood neighbourhood);

    Optional<Neighbourhood> findById(NeighbourhoodId id);

    Optional<Neighbourhood> findByCityIdAndSlug(CityId cityId, String slug);

    List<Neighbourhood> findByCityId(CityId cityId);

    List<Neighbourhood> findByCityIdAndSlugs(CityId cityId, List<String> slugs);

    boolean existsById(NeighbourhoodId id);

    /** Hierarchy consistency check for Ad creation. */
    boolean existsByIdAndCityId(NeighbourhoodId id, CityId cityId);
}
