package com.nextbuy.adhub.ad.infrastructure.persistence.jpa;

import com.nextbuy.adhub.ad.infrastructure.persistence.entity.AdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdJpaRepository extends JpaRepository<AdEntity, UUID> {
}
