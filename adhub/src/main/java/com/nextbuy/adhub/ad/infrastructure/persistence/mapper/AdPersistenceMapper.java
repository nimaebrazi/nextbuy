package com.nextbuy.adhub.ad.infrastructure.persistence.mapper;

import com.nextbuy.adhub.ad.domain.model.*;
import com.nextbuy.adhub.ad.infrastructure.persistence.entity.AdEntity;
import com.nextbuy.adhub.ad.infrastructure.persistence.entity.EntityFactories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EntityFactories.class)
public interface AdPersistenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", expression = "java(ad.getOwnerId().value())")
    @Mapping(target = "categoryId", expression = "java(ad.getCategoryId().value())")
    @Mapping(target = "title", expression = "java(ad.getTitle().value())")
    @Mapping(target = "description", expression = "java(ad.getDescription() == null ? null : ad.getDescription().value())")
    @Mapping(target = "priceAmount", expression = "java(ad.getPrice().amount())")
    @Mapping(target = "priceCurrency", expression = "java(ad.getPrice().currency())")
    @Mapping(target = "status", expression = "java(ad.getStatus())")
    @Mapping(target = "expiresAt", expression = "java(ad.getExpiresAt())")
    @Mapping(target = "rejectionReason", expression = "java(ad.getRejectionReason())")
    @Mapping(target = "suspensionReason", expression = "java(ad.getSuspensionReason())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "rejectedAt", ignore = true)
    @Mapping(target = "suspendedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    AdEntity toEntity(Ad ad);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ownerId", expression = "java(ad.getOwnerId().value())")
    @Mapping(target = "categoryId", expression = "java(ad.getCategoryId().value())")
    @Mapping(target = "title", expression = "java(ad.getTitle().value())")
    @Mapping(target = "description", expression = "java(ad.getDescription() == null ? null : ad.getDescription().value())")
    @Mapping(target = "priceAmount", expression = "java(ad.getPrice().amount())")
    @Mapping(target = "priceCurrency", expression = "java(ad.getPrice().currency())")
    @Mapping(target = "status", expression = "java(ad.getStatus())")
    @Mapping(target = "expiresAt", expression = "java(ad.getExpiresAt())")
    @Mapping(target = "updatedAt", expression = "java(ad.getUpdatedAt())")
    @Mapping(target = "rejectionReason", expression = "java(ad.getRejectionReason())")
    @Mapping(target = "rejectedAt", expression = "java(ad.getRejectedAt())")
    @Mapping(target = "suspensionReason", expression = "java(ad.getSuspensionReason())")
    @Mapping(target = "suspendedAt", expression = "java(ad.getSuspendedAt())")
    @Mapping(target = "deletedAt", expression = "java(ad.getDeletedAt())")
    void updateEntity(Ad ad, @MappingTarget AdEntity entity);

    default Ad toDomain(AdEntity entity, AdLocation location) {
        return Ad.reconstruct(
                AdId.of(entity.getId()),
                OwnerId.of(entity.getOwnerId()),
                CategoryId.of(entity.getCategoryId()),
                location,
                new Title(entity.getTitle()),
                entity.getDescription() != null ? new Description(entity.getDescription()) : null,
                new com.nextbuy.adhub.shared.domain.Mony(entity.getPriceAmount(), entity.getPriceCurrency()),
                entity.getRejectionReason(),
                entity.getSuspensionReason(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getUpdatedAt(),
                entity.getRejectedAt(),
                entity.getSuspendedAt(),
                entity.getDeletedAt()
        );
    }
}
