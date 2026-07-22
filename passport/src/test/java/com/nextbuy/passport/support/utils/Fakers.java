package com.nextbuy.passport.support.utils;

import net.datafaker.Faker;

public final class Fakers {

    private static final Faker faker = new Faker();

    private Fakers() {
    }

    public static Faker faker() {
        return faker;
    }
}
