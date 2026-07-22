package com.nextbuy.passport.support.fixtures;

import com.nextbuy.passport.dto.RefreshTokenContextDto;
import org.instancio.Instancio;
import org.instancio.Select;

import static com.nextbuy.passport.support.utils.Fakers.faker;

public final class AuthContexts {
    private AuthContexts() {
    }

    public static RefreshTokenContextDto refreshTokenContext() {
        return Instancio.of(RefreshTokenContextDto.class)
                .supply(Select.field(RefreshTokenContextDto::ip), () -> faker().internet().ipV4Address())
                .supply(Select.field(RefreshTokenContextDto::userAgent), () -> faker().internet().userAgent())
                .supply(Select.field(RefreshTokenContextDto::device), () -> faker().device().modelName())
                .create();
    }
}
