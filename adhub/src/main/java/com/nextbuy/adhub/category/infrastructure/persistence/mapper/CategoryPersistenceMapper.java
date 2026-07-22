package com.nextbuy.adhub.category.infrastructure.persistence.mapper;

import com.nextbuy.adhub.category.domain.model.Category;
import com.nextbuy.adhub.category.domain.model.CategoryId;
import com.nextbuy.adhub.category.infrastructure.persistence.entity.CategoryEntity;
import com.nextbuy.adhub.category.infrastructure.persistence.entity.EntityFactories;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;

@Mapper(componentModel = "spring", uses = EntityFactories.class)
public interface CategoryPersistenceMapper {

    @Mapping(target = "id", expression = "java(category.getId().value())")
    @Mapping(target = "parentId", expression = "java(category.getParentId() == null ? null : category.getParentId().value())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CategoryEntity toEntity(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "parentId", expression = "java(category.getParentId() == null ? null : category.getParentId().value())")
    void updateEntity(Category category, @MappingTarget CategoryEntity entity);

    default Category toDomain(CategoryEntity entity) {
        CategoryId parentId = entity.getParentId() == null ? null : CategoryId.of(entity.getParentId());
        return Category.reconstruct(
                CategoryId.of(entity.getId()),
                entity.getName(),
                entity.getSlug(),
                parentId
        );
    }

    @AfterMapping
    default void applyTimestamps(Category category, @MappingTarget CategoryEntity entity) {
        Instant now = Instant.now();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setUpdatedAt(now);
    }
}
