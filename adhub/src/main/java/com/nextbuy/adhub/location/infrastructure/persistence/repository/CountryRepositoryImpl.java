package com.nextbuy.adhub.location.infrastructure.persistence.repository;

import com.nextbuy.adhub.location.domain.model.Country;
import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.domain.repository.CountryRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.jpa.CountryJpaRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.mapper.CountryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CountryRepositoryImpl implements CountryRepository {

    private final CountryJpaRepository jpaRepository;
    private final CountryPersistenceMapper mapper;

    public CountryRepositoryImpl(CountryJpaRepository jpaRepository, CountryPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Country save(Country country) {
        if (country.getId() == null || !country.getId().isAssigned()) {
            var saved = jpaRepository.save(mapper.toEntity(country));
            return mapper.toDomain(saved);
        }

        var entity = jpaRepository.findById(country.getId().valueOrThrow())
                .orElseThrow(() -> new IllegalStateException("Country not found: " + country.getId()));
        mapper.updateEntity(country, entity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Country> findById(CountryId id) {
        return jpaRepository.findById(id.valueOrThrow()).map(mapper::toDomain);
    }

    @Override
    public Optional<Country> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public Optional<Country> findByIsoCode(String isoCode) {
        return jpaRepository.findByIsoCode(isoCode).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(CountryId id) {
        return jpaRepository.existsById(id.valueOrThrow());
    }
}
