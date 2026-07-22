package com.nextbuy.adhub.location.infrastructure.persistence.mapper;

import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.domain.model.Province;
import com.nextbuy.adhub.location.domain.model.ProvinceId;
import com.nextbuy.adhub.location.infrastructure.persistence.entity.ProvinceEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProvincePersistenceMapper {

    public ProvinceEntity toEntity(Province province) {
        ProvinceEntity entity = new ProvinceEntity();
        applyFields(province, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    public void updateEntity(Province province, ProvinceEntity entity) {
        applyFields(province, entity);
        entity.setUpdatedAt(Instant.now());
    }

    public Province toDomain(ProvinceEntity entity) {
        return Province.reconstruct(
                ProvinceId.of(entity.getId()),
                entity.getName(),
                entity.getSlug(),
                CountryId.of(entity.getCountryId()),
                GeometryMapper.toGeoPoint(entity.getCentroid()),
                GeometryMapper.toGeoBoundary(entity.getBoundary()),
                GeometryMapper.toBoundingBox(entity.getBbox())
        );
    }

    private void applyFields(Province province, ProvinceEntity entity) {
        entity.setCountryId(province.getCountryId().valueOrThrow());
        entity.setName(province.getName());
        entity.setSlug(province.getSlug());
        entity.setCentroid(GeometryMapper.toJts(province.getCentroid()));
        entity.setBoundary(GeometryMapper.toJts(province.getBoundary()));
    }
}
