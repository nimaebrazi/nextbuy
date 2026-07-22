package com.nextbuy.passport.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfiguration(
        String secret,
        long accessTokenExpiry,
        long refreshTokenExpiry
) {
}
