package com.nextbuy.adhub.location.domain.repository;

import java.util.List;
import java.util.Optional;

import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.domain.model.Province;
import com.nextbuy.adhub.location.domain.model.ProvinceId;

public interface ProvinceRepository {

    Province save(Province province);

    Optional<Province> findById(ProvinceId id);

    Optional<Province> findByCountryIdAndSlug(CountryId countryId, String slug);

    List<Province> findByCountryId(CountryId countryId);

    boolean existsById(ProvinceId id);

    /** Hierarchy consistency check for Ad creation. */
    boolean existsByIdAndCountryId(ProvinceId id, CountryId countryId);
}
