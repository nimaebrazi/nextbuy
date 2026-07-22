package com.nextbuy.adhub.location.infrastructure.persistence.jpa;

import com.nextbuy.adhub.location.infrastructure.persistence.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.List;
import java.util.Optional;

public interface ProvinceJpaRepository extends JpaRepository<ProvinceEntity, UUID> {

    Optional<ProvinceEntity> findByCountryIdAndSlug(UUID countryId, String slug);

    List<ProvinceEntity> findByCountryId(UUID countryId);

    boolean existsByIdAndCountryId(UUID id, UUID countryId);
}
