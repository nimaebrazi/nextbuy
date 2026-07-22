package com.nextbuy.passport.repository.jpa;

import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return jpaRefreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRefreshTokenRepository.findByTokenHash(tokenHash);
    }

    @Override
    public void revokeByTokenHash(String tokenHash) {
        Optional<RefreshToken> refreshToken = jpaRefreshTokenRepository.findByTokenHash(tokenHash);

        refreshToken.filter(RefreshToken::isActive).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            jpaRefreshTokenRepository.save(token);
        });
    }

    @Override
    public Optional<RefreshToken> findByTokenHashIncludingRevoked(String tokenHash) {
        return jpaRefreshTokenRepository.findByTokenHashIncludingRevoked(tokenHash);
    }

    @Override
    public void revokeAllActiveByUserId(Long userId) {
        jpaRefreshTokenRepository.revokeAllActiveByUserId(userId, Instant.now());
    }
}
