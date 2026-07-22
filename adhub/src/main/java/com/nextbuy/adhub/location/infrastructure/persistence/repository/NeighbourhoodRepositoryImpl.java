package com.nextbuy.adhub.location.infrastructure.persistence.repository;

import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.Neighbourhood;
import com.nextbuy.adhub.location.domain.model.NeighbourhoodId;
import com.nextbuy.adhub.location.domain.repository.NeighbourhoodRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.jpa.NeighbourhoodJpaRepository;
import com.nextbuy.adhub.location.infrastructure.persistence.mapper.NeighbourhoodPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NeighbourhoodRepositoryImpl implements NeighbourhoodRepository {

    private final NeighbourhoodJpaRepository jpaRepository;
    private final NeighbourhoodPersistenceMapper mapper;

    public NeighbourhoodRepositoryImpl(NeighbourhoodJpaRepository jpaRepository,
                                       NeighbourhoodPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Neighbourhood save(Neighbourhood neighbourhood) {
        if (neighbourhood.getId() == null || !neighbourhood.getId().isAssigned()) {
            var saved = jpaRepository.save(mapper.toEntity(neighbourhood));
            return mapper.toDomain(saved);
        }

        var entity = jpaRepository.findById(neighbourhood.getId().valueOrThrow())
                .orElseThrow(() -> new IllegalStateException("Neighbourhood not found: " + neighbourhood.getId()));
        mapper.updateEntity(neighbourhood, entity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Neighbourhood> findById(NeighbourhoodId id) {
        return jpaRepository.findById(id.valueOrThrow()).map(mapper::toDomain);
    }

    @Override
    public Optional<Neighbourhood> findByCityIdAndSlug(CityId cityId, String slug) {
        return jpaRepository.findByCityIdAndSlug(cityId.valueOrThrow(), slug).map(mapper::toDomain);
    }

    @Override
    public List<Neighbourhood> findByCityId(CityId cityId) {
        return jpaRepository.findByCityId(cityId.valueOrThrow()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Neighbourhood> findByCityIdAndSlugs(CityId cityId, List<String> slugs) {
        return jpaRepository.findByCityIdAndSlugIn(cityId.valueOrThrow(), slugs).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(NeighbourhoodId id) {
        return jpaRepository.existsById(id.valueOrThrow());
    }

    @Override
    public boolean existsByIdAndCityId(NeighbourhoodId id, CityId cityId) {
        return jpaRepository.existsByIdAndCityId(id.valueOrThrow(), cityId.valueOrThrow());
    }
}
