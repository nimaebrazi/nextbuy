package com.nextbuy.passport.support.utils;

import net.datafaker.Faker;

public final class Fakers {

    private static final Faker faker = new Faker();

    private Fakers() {
    }

    public static Faker faker() {
        return faker;
    }

    public static String randomPassword() {
        return faker.internet().password(8, 16, true, true, true);
    }
}
