package com.nextbuy.adhub.location.infrastructure.persistence.jpa;

import com.nextbuy.adhub.location.infrastructure.persistence.entity.NeighbourhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.List;
import java.util.Optional;

public interface NeighbourhoodJpaRepository extends JpaRepository<NeighbourhoodEntity, UUID> {

    Optional<NeighbourhoodEntity> findByCityIdAndSlug(UUID cityId, String slug);

    List<NeighbourhoodEntity> findByCityId(UUID cityId);

    List<NeighbourhoodEntity> findByCityIdAndSlugIn(UUID cityId, List<String> slugs);

    boolean existsByIdAndCityId(UUID id, UUID cityId);
}
