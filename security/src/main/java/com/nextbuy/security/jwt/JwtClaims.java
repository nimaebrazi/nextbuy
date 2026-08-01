package com.nextbuy.security.jwt;

public record JwtClaims(
        Long userId,
        String email,
        String roles,
        String permissions
) {
}
