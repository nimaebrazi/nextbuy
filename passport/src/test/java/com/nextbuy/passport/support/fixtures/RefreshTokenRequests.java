package com.nextbuy.passport.support.fixtures;

import com.nextbuy.passport.dto.RefreshTokenRequestDto;
import org.instancio.Instancio;
import org.instancio.Select;

public final class RefreshTokenRequests {
    private RefreshTokenRequests() {
    }

    public static RefreshTokenRequestDto random() {
        return Instancio.of(RefreshTokenRequestDto.class)
                .supply(Select.field(RefreshTokenRequestDto::refreshToken), AuthTokens::refreshToken)
                .create();
    }

    public static RefreshTokenRequestDto withToken(String refreshToken) {
        return new RefreshTokenRequestDto(refreshToken);
    }
}
