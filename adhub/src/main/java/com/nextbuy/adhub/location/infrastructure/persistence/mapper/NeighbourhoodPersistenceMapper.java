package com.nextbuy.adhub.location.infrastructure.persistence.mapper;

import com.nextbuy.adhub.location.domain.model.BoundingBox;
import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.Neighbourhood;
import com.nextbuy.adhub.location.domain.model.NeighbourhoodId;
import com.nextbuy.adhub.location.infrastructure.persistence.entity.NeighbourhoodEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NeighbourhoodPersistenceMapper {

    public NeighbourhoodEntity toEntity(Neighbourhood neighbourhood) {
        NeighbourhoodEntity entity = new NeighbourhoodEntity();
        applyFields(neighbourhood, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    public void updateEntity(Neighbourhood neighbourhood, NeighbourhoodEntity entity) {
        applyFields(neighbourhood, entity);
        entity.setUpdatedAt(Instant.now());
    }

    public Neighbourhood toDomain(NeighbourhoodEntity entity) {
        return Neighbourhood.reconstruct(
                NeighbourhoodId.of(entity.getId()),
                entity.getName(),
                entity.getSlug(),
                CityId.of(entity.getCityId()),
                GeometryMapper.toGeoPoint(entity.getCentroid()),
                GeometryMapper.toGeoBoundary(entity.getBoundary()),
                BoundingBox.ofNullable(
                        entity.getBboxMinLng(), entity.getBboxMinLat(),
                        entity.getBboxMaxLng(), entity.getBboxMaxLat())
        );
    }

    private void applyFields(Neighbourhood neighbourhood, NeighbourhoodEntity entity) {
        entity.setCityId(neighbourhood.getCityId().valueOrThrow());
        entity.setName(neighbourhood.getName());
        entity.setSlug(neighbourhood.getSlug());
        entity.setCentroid(GeometryMapper.toJts(neighbourhood.getCentroid()));
        entity.setBoundary(GeometryMapper.toJts(neighbourhood.getBoundary()));
    }
}
