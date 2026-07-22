package com.nextbuy.adhub.location.domain.repository;

import java.util.Optional;

import com.nextbuy.adhub.location.domain.model.Country;
import com.nextbuy.adhub.location.domain.model.CountryId;

public interface CountryRepository {

    Country save(Country country);

    Optional<Country> findById(CountryId id);

    Optional<Country> findBySlug(String slug);

    Optional<Country> findByIsoCode(String isoCode);

    boolean existsById(CountryId id);
}
