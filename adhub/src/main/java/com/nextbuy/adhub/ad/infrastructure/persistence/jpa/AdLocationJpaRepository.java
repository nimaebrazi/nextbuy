package com.nextbuy.adhub.ad.infrastructure.persistence.jpa;

import com.nextbuy.adhub.ad.infrastructure.persistence.entity.AdLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdLocationJpaRepository extends JpaRepository<AdLocationEntity, UUID> {
}
