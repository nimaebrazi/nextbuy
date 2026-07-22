package com.nextbuy.adhub.location.infrastructure.persistence.repository;

import com.nextbuy.adhub.location.domain.model.City;
import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.ProvinceId;
import com.nextbuy.adhub.location.domain.repository.CityRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.jpa.CityJpaRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.mapper.CityPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CityRepositoryImpl implements CityRepository {

    private final CityJpaRepository jpaRepository;
    private final CityPersistenceMapper mapper;

    public CityRepositoryImpl(CityJpaRepository jpaRepository, CityPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public City save(City city) {
        if (city.getId() == null || !city.getId().isAssigned()) {
            var saved = jpaRepository.save(mapper.toEntity(city));
            return mapper.toDomain(saved);
        }

        var entity = jpaRepository.findById(city.getId().valueOrThrow())
                .orElseThrow(() -> new IllegalStateException("City not found: " + city.getId()));
        mapper.updateEntity(city, entity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<City> findById(CityId id) {
        return jpaRepository.findById(id.valueOrThrow()).map(mapper::toDomain);
    }

    @Override
    public Optional<City> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public Optional<City> findByProvinceIdAndSlug(ProvinceId provinceId, String slug) {
        return jpaRepository.findByProvinceIdAndSlug(provinceId.valueOrThrow(), slug).map(mapper::toDomain);
    }

    @Override
    public List<City> findByProvinceId(ProvinceId provinceId) {
        return jpaRepository.findByProvinceId(provinceId.valueOrThrow()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(CityId id) {
        return jpaRepository.existsById(id.valueOrThrow());
    }

    @Override
    public boolean existsByIdAndProvinceId(CityId id, ProvinceId provinceId) {
        return jpaRepository.existsByIdAndProvinceId(id.valueOrThrow(), provinceId.valueOrThrow());
    }
}
