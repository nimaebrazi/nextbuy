package com.nextbuy.passport.support.fixtures;

import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.support.utils.Fakers;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import static com.nextbuy.passport.support.utils.Fakers.faker;


public final class Users {

    public static final String DEFAULT_PASSWORD = "TestPass1";

    private Users() {
    }

    public static User random() {
        return customize(spec -> spec.set(Select.field(User::getRoles), new HashSet<>()));
    }

    public static User withEmail(String email) {
        return withEmailAndPassword(email, DEFAULT_PASSWORD);
    }

    public static User withIdAndEmail(String email, String encodedPassword) {
        return Instancio.of(User.class)
                .set(Select.field(User::getId), faker().number().numberBetween(1L, Long.MAX_VALUE))
                .set(Select.field(User::getEmail), email)
                .set(Select.field(User::getPassword), encodedPassword)
                .set(Select.field(User::getRoles), new HashSet<>())
                .create();
    }

    public static User randomWithId() {
        return Instancio.of(User.class)
                .set(Select.field(User::getId), faker().number().numberBetween(1L, Long.MAX_VALUE))
                .set(Select.field(User::getEmail), faker().internet().emailAddress())
                .set(Select.field(User::getPassword), Fakers.randomPassword())
                .set(Select.field(User::getRoles), new HashSet<>())
                .create();
    }


    public static User withEmailAndPassword(String email, String password) {
        return customize(spec -> spec
                .set(Select.field(User::getEmail), email)
                .set(Select.field(User::getPassword), password)
                .set(Select.field(User::getRoles), new HashSet<>()));
    }

    public static User withRoles(String email, String password, Collection<Role> roles) {
        return customize(spec -> spec
                .set(Select.field(User::getEmail), email)
                .set(Select.field(User::getPassword), password)
                .set(Select.field(User::getRoles), new HashSet<>(roles)));
    }

    public static User withRandomRoles(int roleCount) {
        return customize(spec -> spec
                .set(Select.field(User::getRoles), Roles.randomSet(roleCount)));
    }

    private static User customize(Consumer<InstancioApi<User>> customizer) {
        var spec = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getBannedAt))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getEmail), () -> faker().internet().emailAddress())
                .supply(Select.field(User::getPassword), Fakers::randomPassword)
                .set(Select.field(User::getRoles), new HashSet<>());
        customizer.accept(spec);
        return spec.create();
    }
}