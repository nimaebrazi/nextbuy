package com.nextbuy.adhub.location.infrastructure.persistence.jpa;

import com.nextbuy.adhub.location.infrastructure.persistence.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.List;
import java.util.Optional;

public interface CityJpaRepository extends JpaRepository<CityEntity, UUID> {

    Optional<CityEntity> findBySlug(String slug);

    Optional<CityEntity> findByProvinceIdAndSlug(UUID provinceId, String slug);

    List<CityEntity> findByProvinceId(UUID provinceId);

    boolean existsByIdAndProvinceId(UUID id, UUID provinceId);
}
