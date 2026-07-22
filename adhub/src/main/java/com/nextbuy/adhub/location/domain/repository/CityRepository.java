package com.nextbuy.adhub.location.domain.repository;

import java.util.List;
import java.util.Optional;

import com.nextbuy.adhub.location.domain.model.City;
import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.ProvinceId;

public interface CityRepository {

    City save(City city);

    Optional<City> findById(CityId id);

    Optional<City> findBySlug(String slug);

    Optional<City> findByProvinceIdAndSlug(ProvinceId provinceId, String slug);

    List<City> findByProvinceId(ProvinceId provinceId);

    boolean existsById(CityId id);

    boolean existsByIdAndProvinceId(CityId id, ProvinceId provinceId);
}
