package com.nextbuy.adhub.ad.infrastructure.persistence.repository;

import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import com.nextbuy.adhub.ad.infrastructure.persistence.entity.AdEntity;
import com.nextbuy.adhub.ad.infrastructure.persistence.jpa.AdJpaRepository;
import com.nextbuy.adhub.ad.infrastructure.persistence.jpa.AdLocationJpaRepository;
import com.nextbuy.adhub.ad.infrastructure.persistence.mapper.AdLocationPersistenceMapper;
import com.nextbuy.adhub.ad.infrastructure.persistence.mapper.AdPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdRepositoryImpl implements AdRepository {

    private final AdJpaRepository adJpaRepository;
    private final AdLocationJpaRepository adLocationJpaRepository;
    private final AdPersistenceMapper adMapper;
    private final AdLocationPersistenceMapper locationMapper;

    public AdRepositoryImpl(AdJpaRepository adJpaRepository,
                            AdLocationJpaRepository adLocationJpaRepository,
                            AdPersistenceMapper adMapper,
                            AdLocationPersistenceMapper locationMapper) {
        this.adJpaRepository = adJpaRepository;
        this.adLocationJpaRepository = adLocationJpaRepository;
        this.adMapper = adMapper;
        this.locationMapper = locationMapper;
    }

    @Override
    public AdId saveNew(Ad ad) {
        if (ad.getId().isAssigned()) {
            throw new IllegalArgumentException("saveNew requires an unassigned Ad id");
        }
        var entity = adMapper.toEntity(ad);
        entity.setCreatedAt(ad.getCreatedAt());
        entity.setUpdatedAt(ad.getUpdatedAt());
        var saved = adJpaRepository.save(entity);
        adLocationJpaRepository.save(locationMapper.toEntity(saved.getId(), ad.getLocation()));
        return AdId.of(saved.getId());
    }

    @Override
    public Ad save(Ad ad) {
        if (!ad.getId().isAssigned()) {
            throw new IllegalArgumentException("save requires an assigned Ad id; use saveNew for inserts");
        }
        AdEntity adEntity = updateExisting(ad);

        var existingLocation = adLocationJpaRepository.findById(adEntity.getId());
        if (existingLocation.isPresent()) {
            var locationEntity = existingLocation.get();
            locationMapper.updateEntity(ad.getLocation(), locationEntity);
            adLocationJpaRepository.save(locationEntity);
        } else {
            adLocationJpaRepository.save(locationMapper.toEntity(adEntity.getId(), ad.getLocation()));
        }

        return ad;
    }

    @Override
    public Optional<Ad> findById(AdId id) {
        return adJpaRepository.findById(id.valueOrThrow())
                .flatMap(adEntity -> adLocationJpaRepository.findById(adEntity.getId())
                        .map(locationEntity ->
                                adMapper.toDomain(adEntity, locationMapper.toDomain(locationEntity))));
    }

    private AdEntity updateExisting(Ad ad) {
        var entity = adJpaRepository.findById(ad.getId().valueOrThrow())
                .orElseThrow(() -> new IllegalStateException("Ad not found: " + ad.getId()));
        adMapper.updateEntity(ad, entity);
        return adJpaRepository.save(entity);
    }
}
