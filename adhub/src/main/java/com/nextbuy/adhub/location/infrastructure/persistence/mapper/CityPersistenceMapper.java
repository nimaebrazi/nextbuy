package com.nextbuy.adhub.location.infrastructure.persistence.mapper;

import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.City;
import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.ProvinceId;
import com.nextbuy.adhub.location.infrastructure.persistence.entity.CityEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CityPersistenceMapper {

    public CityEntity toEntity(City city) {
        CityEntity entity = new CityEntity();
        applyFields(city, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    public void updateEntity(City city, CityEntity entity) {
        applyFields(city, entity);
        entity.setUpdatedAt(Instant.now());
    }

    public City toDomain(CityEntity entity) {
        return City.reconstruct(
                CityId.of(entity.getId()),
                entity.getName(),
                entity.getSlug(),
                ProvinceId.of(entity.getProvinceId()),
                GeometryMapper.toGeoPoint(entity.getCentroid()),
                GeometryMapper.toGeoBoundary(entity.getBoundary()),
                BoundingBox.ofNullable(
                        entity.getBboxMinLng(), entity.getBboxMinLat(),
                        entity.getBboxMaxLng(), entity.getBboxMaxLat())
        );
    }

    private void applyFields(City city, CityEntity entity) {
        entity.setProvinceId(city.getProvinceId().valueOrThrow());
        entity.setName(city.getName());
        entity.setSlug(city.getSlug());
        entity.setCentroid(GeometryMapper.toJts(city.getCentroid()));
        entity.setBoundary(GeometryMapper.toJts(city.getBoundary()));
    }
}
