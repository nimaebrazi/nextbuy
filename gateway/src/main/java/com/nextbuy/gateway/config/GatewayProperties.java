package com.nextbuy.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "nextbuy.gateway")
public record GatewayProperties(
        String passportUri,
        String adhubUri
) {}