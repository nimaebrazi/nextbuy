package com.nextbuy.passport.repository;

import com.nextbuy.passport.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeByTokenHash(String tokenHash);
    Optional<RefreshToken> findByTokenHashIncludingRevoked(String tokenHash);
    void revokeAllActiveByUserId(Long userId);
}
