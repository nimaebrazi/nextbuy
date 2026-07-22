package com.nextbuy.passport.service;


import com.nextbuy.passport.configuration.RateLimitProperties;
import com.nextbuy.passport.dto.RateLimitResult;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class RateLimitService {

    private final ProxyManager<byte[]> proxyManager;
    private final Supplier<BucketConfiguration> ipBucketConfiguration;
    private final Supplier<BucketConfiguration> userBucketConfiguration;

    public RateLimitService(ProxyManager<byte[]> proxyManager, RateLimitProperties properties) {
        this.proxyManager = proxyManager;

        this.ipBucketConfiguration = () -> toBucketConfiguration(properties.ip());
        this.userBucketConfiguration = () -> toBucketConfiguration(properties.user());

    }

    public RateLimitResult checkIp(String ip) {
        String key = "login:ip:" + ip;
        return check(key, ipBucketConfiguration);
    }

    public RateLimitResult checkUser(String email) {
        String key = "login:user:" + email;
        return check(key, userBucketConfiguration);
    }

    private RateLimitResult check(String key, Supplier<BucketConfiguration> config) {
        var bucket = proxyManager.builder().build(key.getBytes(), config);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return RateLimitResult.allowed(probe.getRemainingTokens());
        }
        long retryAfterSeconds = Math.max(
                1,
                (probe.getNanosToWaitForRefill() + 999_999_999) / 1_000_000_000
        );
        return RateLimitResult.blocked(retryAfterSeconds);
    }

    private static BucketConfiguration toBucketConfiguration(RateLimitProperties.LimitConfig limit) {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(limit.capacity())
                .refillGreedy(limit.refillAmount(), Duration.ofSeconds(limit.refillDuration()))
                .build();
        return BucketConfiguration.builder()
                .addLimit(bandwidth)
                .build();
    }
}
