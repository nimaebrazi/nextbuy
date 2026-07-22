package com.nextbuy.passport.support.fixtures;

import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.domain.User;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.util.*;
import java.util.function.Consumer;

import static com.nextbuy.passport.support.utils.Fakers.faker;

public final class Roles {

    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";

    private static final int MAX_DESCRIPTION_LENGTH = 255;

    private Roles() {
    }

    public static Role random() {
        return customize(spec -> {
        });
    }

    public static Role named(String name, String description) {
        return customize(spec -> spec
                .set(Select.field(Role::getName), name)
                .set(Select.field(Role::getDescription), description));
    }

    public static Role named(String name) {
        return named(name, "description for " + name);
    }

    public static Role withIdAndName(String name) {
        return Instancio.of(Role.class)
                .set(Select.field(Role::getId), faker().number().numberBetween(1L, Long.MAX_VALUE))
                .supply(Select.field(Role::getName), () -> name)
                .supply(Select.field(Role::getDescription), Roles::randomDescription)
                .set(Select.field(Role::getPermissions), new HashSet<>())
                .create();
    }

    public static Role withIdAndNameAndDescription(String name, String description) {
        return Instancio.of(Role.class)
                .set(Select.field(Role::getId), faker().number().numberBetween(1L, Long.MAX_VALUE))
                .supply(Select.field(Role::getName), () -> name)
                .supply(Select.field(Role::getDescription), () -> description)
                .set(Select.field(Role::getPermissions), new HashSet<>())
                .create();
    }

    public static List<Role> named(String... names) {
        return Arrays.stream(names).map(Roles::named).toList();
    }

    public static List<Role> random(int count) {
        return faker().collection(Roles::random).len(count).generate();
    }

    public static Set<Role> randomSet(int count) {
        return new HashSet<>(random(count));
    }

    private static Role customize(Consumer<InstancioApi<Role>> customizer) {
        var spec = Instancio.of(Role.class)
                .ignore(Select.field(Role::getId))
                .supply(Select.field(Role::getName), Roles::randomName)
                .supply(Select.field(Role::getDescription), Roles::randomDescription)
                .set(Select.field(Role::getPermissions), new HashSet<>());
        customizer.accept(spec);
        return spec.create();
    }

    private static String randomName() {
        return "ROLE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private static String randomDescription() {
        return faker().text().text(Math.min(50, MAX_DESCRIPTION_LENGTH));
    }
}