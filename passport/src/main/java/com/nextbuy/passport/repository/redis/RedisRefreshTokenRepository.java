package com.nextbuy.passport.repository.redis;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh-token:";

    private final StringRedisTemplate redisTemplate;
    private final JsonMapper objectMapper;

    public void save(RefreshTokenCacheDto token) {
        try {

            Duration ttl = Duration.between(Instant.now(), token.expiresAt());

            if (ttl.isNegative() || ttl.isZero()) {
                return;
            }

            var key = key(token.tokenHash());
            var value = objectMapper.writeValueAsString(token);

            redisTemplate.opsForValue().set(key, value, ttl);

        } catch (Exception e) {
            throw new IllegalStateException("Could not cache refresh token", e);
        }
    }

    public Optional<RefreshTokenCacheDto> findByTokenHash(String tokenHash) {
        try {
            String value = redisTemplate.opsForValue().get(key(tokenHash));
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, RefreshTokenCacheDto.class));
        } catch (Exception e) {
            redisTemplate.delete(key(tokenHash));
            return Optional.empty();
        }
    }

    public void deleteByTokenHash(String tokenHash) {
        redisTemplate.delete(key(tokenHash));
    }

    private String key(String tokenHash) {
        return KEY_PREFIX + tokenHash;
    }
}
