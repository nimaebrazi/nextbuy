package com.nextbuy.adhub.ad.infrastructure.persistence.mapper;

import com.nextbuy.adhub.ad.domain.model.AdLocation;
import com.nextbuy.adhub.ad.infrastructure.persistence.entity.AdLocationEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AdLocationPersistenceMapper {

    public AdLocationEntity toEntity(UUID adId, AdLocation location) {
        AdLocationEntity entity = new AdLocationEntity();
        entity.setAdId(adId);
        applyFields(location, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    public void updateEntity(AdLocation location, AdLocationEntity entity) {
        applyFields(location, entity);
        entity.setUpdatedAt(Instant.now());
    }

    public AdLocation toDomain(AdLocationEntity entity) {
        Double latitude = null;
        Double longitude = null;
        if (entity.getPoint() != null) {
            double[] coords = AdLocationPointMapper.fromJts(entity.getPoint());
            latitude = coords[0];
            longitude = coords[1];
        }
        return AdLocation.of(
                entity.getCountryId(),
                entity.getProvinceId(),
                entity.getCityId(),
                entity.getNeighbourhoodId(),
                latitude,
                longitude
        );
    }

    private void applyFields(AdLocation location, AdLocationEntity entity) {
        entity.setCountryId(location.countryId());
        entity.setProvinceId(location.provinceId());
        entity.setCityId(location.cityId());
        entity.setNeighbourhoodId(location.neighbourhoodId().orElse(null));
        if (location.hasExactLocation()) {
            entity.setPoint(AdLocationPointMapper.toJts(
                    location.latitude().orElseThrow(),
                    location.longitude().orElseThrow()
            ));
        } else {
            entity.setPoint(null);
        }
    }
}
