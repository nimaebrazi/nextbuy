package com.nextbuy.adhub.ad.infrastructure.persistence.entity;


import org.mapstruct.ObjectFactory;

public final class EntityFactories {

    private EntityFactories() {
    }

    @ObjectFactory
    public static AdEntity createAdEntity() {
        return new AdEntity();
    }
}
