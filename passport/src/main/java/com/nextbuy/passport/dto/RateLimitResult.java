package com.nextbuy.passport.dto;

public record RateLimitResult(
        boolean allowed,
        long remainingTokens,
        long retryAfterSeconds
) {
    public static RateLimitResult allowed(long remaining) {
        return new RateLimitResult(true, remaining, 0);
    }

    public static RateLimitResult blocked(long retryAfter) {
        return new RateLimitResult(false, 0, retryAfter);
    }
}