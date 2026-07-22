package com.nextbuy.adhub.location.infrastructure.persistence.mapper;

import com.nextbuy.adhub.location.domain.model.Country;
import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.infrastructure.persistence.entity.CountryEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CountryPersistenceMapper {

    public CountryEntity toEntity(Country country) {
        CountryEntity entity = new CountryEntity();
        applyFields(country, entity);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    public void updateEntity(Country country, CountryEntity entity) {
        applyFields(country, entity);
        entity.setUpdatedAt(Instant.now());
    }

    public Country toDomain(CountryEntity entity) {
        return Country.reconstruct(
                CountryId.of(entity.getId()),
                entity.getName(),
                entity.getIsoCode(),
                entity.getSlug(),
                GeometryMapper.toGeoPoint(entity.getCentroid()),
                GeometryMapper.toGeoBoundary(entity.getBoundary()),
                GeometryMapper.toBoundingBox(entity.getBbox())
        );
    }

    private void applyFields(Country country, CountryEntity entity) {
        entity.setName(country.getName());
        entity.setIsoCode(country.getIsoCode());
        entity.setSlug(country.getSlug());
        entity.setCentroid(GeometryMapper.toJts(country.getCentroid()));
        entity.setBoundary(GeometryMapper.toJts(country.getBoundary()));
    }
}
