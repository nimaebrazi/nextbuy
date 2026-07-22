package com.nextbuy.passport.repository.redis;

import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.User;

import java.time.Instant;

public record RefreshTokenCacheDto(
        Long refreshTokenId,
        Long userId,
        String email,
        String tokenHash,
        Instant expiresAt,
        Instant revokedAt,
        String ip,
        String userAgent,
        String device
) {
    public boolean isActive() {
        return revokedAt == null && expiresAt.isAfter(Instant.now());
    }

    public static RefreshTokenCacheDto from(RefreshToken token) {
        return new RefreshTokenCacheDto(
                token.getId(),
                token.getUser().getId(),
                token.getUser().getEmail(),
                token.getTokenHash(),
                token.getExpiresAt(),
                token.getRevokedAt(),
                token.getIp(),
                token.getUserAgent(),
                token.getDevice()
        );
    }

    public RefreshToken toEntity() {
        return RefreshToken.builder()
                .id(refreshTokenId)
                .user(User.builder().id(userId).email(email).build())
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .revokedAt(revokedAt)
                .ip(ip)
                .userAgent(userAgent)
                .device(device)
                .build();
    }
}