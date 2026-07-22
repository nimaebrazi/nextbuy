package com.nextbuy.adhub.support.shared;

import net.datafaker.Faker;
import net.datafaker.providers.base.Text;

import java.util.UUID;

public final class Fakers {

    private static final Faker faker = new Faker();

    private Fakers() {
    }

    public static Faker faker() {
        return faker;
    }

    public static String randomPassword() {
        return faker.text().text(Text.TextSymbolsBuilder.builder()
                .len(12)
                .with(Text.EN_UPPERCASE, 2)
                .with(Text.DIGITS, 3)
                .build());
    }

    public static String uuid(){
        return UUID.randomUUID().toString();
    }
}
