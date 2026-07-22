package com.nextbuy.passport.support.fixtures;

import com.nextbuy.passport.dto.AuthTokenResponseDto;

import static com.nextbuy.passport.support.utils.Fakers.faker;

public final class AuthTokens {
    private AuthTokens() {
    }

    public static String accessToken() {
        return faker().regexify("[a-zA-Z0-9_-]{32}");
    }

    public static String refreshToken() {
        return faker().regexify("[a-zA-Z0-9_-]{64}");
    }

    public static AuthTokenResponseDto response() {
        return new AuthTokenResponseDto(accessToken(), refreshToken());
    }

    public static AuthTokenResponseDto response(String accessToken, String refreshToken) {
        return new AuthTokenResponseDto(accessToken, refreshToken);
    }
}
