package com.nextbuy.passport.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bucket4j.rate-limit.login")
public record RateLimitProperties(

        LimitConfig ip,
        LimitConfig user

) {
    public record LimitConfig(
            int capacity,
            int refillAmount,
            int refillDuration
    ) {
    }
}
