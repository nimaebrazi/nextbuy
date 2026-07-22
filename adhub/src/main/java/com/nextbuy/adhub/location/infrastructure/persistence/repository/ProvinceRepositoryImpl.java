package com.nextbuy.adhub.location.infrastructure.persistence.repository;

import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.domain.model.Province;
import com.nextbuy.adhub.location.domain.model.ProvinceId;
import com.nextbuy.adhub.location.domain.repository.ProvinceRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.jpa.ProvinceJpaRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.mapper.ProvincePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProvinceRepositoryImpl implements ProvinceRepository {

    private final ProvinceJpaRepository jpaRepository;
    private final ProvincePersistenceMapper mapper;

    public ProvinceRepositoryImpl(ProvinceJpaRepository jpaRepository, ProvincePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Province save(Province province) {
        if (province.getId() == null || !province.getId().isAssigned()) {
            var saved = jpaRepository.save(mapper.toEntity(province));
            return mapper.toDomain(saved);
        }

        var entity = jpaRepository.findById(province.getId().valueOrThrow())
                .orElseThrow(() -> new IllegalStateException("Province not found: " + province.getId()));
        mapper.updateEntity(province, entity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Province> findById(ProvinceId id) {
        return jpaRepository.findById(id.valueOrThrow()).map(mapper::toDomain);
    }

    @Override
    public Optional<Province> findByCountryIdAndSlug(CountryId countryId, String slug) {
        return jpaRepository.findByCountryIdAndSlug(countryId.valueOrThrow(), slug).map(mapper::toDomain);
    }

    @Override
    public List<Province> findByCountryId(CountryId countryId) {
        return jpaRepository.findByCountryId(countryId.valueOrThrow()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(ProvinceId id) {
        return jpaRepository.existsById(id.valueOrThrow());
    }

    @Override
    public boolean existsByIdAndCountryId(ProvinceId id, CountryId countryId) {
        return jpaRepository.existsByIdAndCountryId(id.valueOrThrow(), countryId.valueOrThrow());
    }
}
