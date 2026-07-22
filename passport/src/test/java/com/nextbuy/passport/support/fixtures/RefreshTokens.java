package com.nextbuy.passport.support.fixtures;

import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.domain.User;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.time.Instant;
import java.util.function.Consumer;

import static com.nextbuy.passport.support.utils.Fakers.faker;

public final class RefreshTokens {


    private RefreshTokens() {
    }

    public static RefreshToken random() {
        return customize(spec -> spec
                .ignore(Select.field(RefreshToken::getUser))); // force caller to set user via forUser
    }

    public static RefreshToken forUser(User user, String tokenHash) {
        return customize(spec -> spec
                .set(Select.field(RefreshToken::getUser), user)
                .set(Select.field(RefreshToken::getTokenHash), tokenHash));
    }

    public static RefreshToken withTokenHash(String tokenHash) {
        return customize(spec -> spec
                .ignore(Select.field(RefreshToken::getRevokedAt))

                .set(Select.field(RefreshToken::getTokenHash), tokenHash));
    }

    public static RefreshToken revokedAt(Instant revokedAt) {
        return customize(spec -> spec
                .set(Select.field(RefreshToken::getId), faker().number().numberBetween(1L, Long.MAX_VALUE))
                .set(Select.field(RefreshToken::getRevokedAt), revokedAt)
        );
    }

    private static RefreshToken customize(Consumer<InstancioApi<RefreshToken>> customizer) {
        var spec = Instancio.of(RefreshToken.class)
                .ignore(Select.field(RefreshToken::getId))
                .ignore(Select.field(RefreshToken::getRevokedAt))
                .supply(Select.field(RefreshToken::getTokenHash), () -> faker().regexify("[a-f0-9]{64}"))
                .supply(Select.field(RefreshToken::getCreatedAt), () -> Instant.now())
                .supply(Select.field(RefreshToken::getUserAgent), () -> faker().internet().userAgent())
                .supply(Select.field(RefreshToken::getDevice), () -> faker().device().manufacturer())
                .supply(Select.field(RefreshToken::getIp), () -> faker().internet().ipV4Address())
                .supply(Select.field(RefreshToken::getExpiresAt), () -> Instant.now().plusSeconds(3600));
        customizer.accept(spec);
        return spec.create();
    }
}