package com.nextbuy.passport.dto;

public record RefreshTokenContextDto(
        String ip,
        String userAgent,
        String device
) {
}