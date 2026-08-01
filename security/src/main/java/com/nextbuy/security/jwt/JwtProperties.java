package com.nextbuy.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration. {@code accessTokenExpiry} and {@code refreshTokenExpiry} are in seconds.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpiry,
        long refreshTokenExpiry
) {
}
