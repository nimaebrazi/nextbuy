package com.nextbuy.passport.dto;

public record AuthTokenResponseDto(
        String accessToken,
        String refreshToken
) {
}