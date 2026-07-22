package com.nextbuy.passport.support.fixtures;


import com.nextbuy.passport.dto.RateLimitResult;


public final class RateLimitResults {
    private RateLimitResults() {}

    public static RateLimitResult allowed(long remaining) {
        return RateLimitResult.allowed(remaining);
    }
    public static RateLimitResult blocked(long retryAfterSeconds) {
        return RateLimitResult.blocked(retryAfterSeconds);
    }
}
