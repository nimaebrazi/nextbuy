package com.nextbuy.adhub.location.infrastructure.persistence.jpa;

import com.nextbuy.adhub.location.infrastructure.persistence.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.Optional;

public interface CountryJpaRepository extends JpaRepository<CountryEntity, UUID> {

    Optional<CountryEntity> findBySlug(String slug);

    Optional<CountryEntity> findByIsoCode(String isoCode);
}
