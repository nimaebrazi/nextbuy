package com.nextbuy.adhub.category.infrastructure.persistence.entity;

import org.mapstruct.ObjectFactory;

public final class EntityFactories {

    private EntityFactories() {
    }

    @ObjectFactory
    public static CategoryEntity createCategoryEntity() {
        return new CategoryEntity();
    }
}
