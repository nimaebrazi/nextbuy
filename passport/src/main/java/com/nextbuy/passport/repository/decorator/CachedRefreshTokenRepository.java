package com.nextbuy.passport.repository.decorator;


import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.repository.AfterCommitExecutor;
import com.nextbuy.passport.repository.RefreshTokenRepository;
import com.nextbuy.passport.repository.redis.RedisRefreshTokenRepository;
import com.nextbuy.passport.repository.redis.RefreshTokenCacheDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public class CachedRefreshTokenRepository implements RefreshTokenRepository {

    private final RefreshTokenRepository delegate;
    private final AfterCommitExecutor afterCommitExecutor;
    private final RedisRefreshTokenRepository redisRepository;

    public CachedRefreshTokenRepository(
            @Qualifier("jpaRefreshTokenRepositoryAdapter") RefreshTokenRepository delegate,
            RedisRefreshTokenRepository redisRepository,
            AfterCommitExecutor afterCommitExecutor
    ) {
        this.delegate = delegate;
        this.redisRepository = redisRepository;
        this.afterCommitExecutor = afterCommitExecutor;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshToken saved = delegate.save(refreshToken);

        afterCommitExecutor.run(() -> {
            var dto = RefreshTokenCacheDto.from(saved);
            redisRepository.save(dto);
        });

        return saved;
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        Optional<RefreshTokenCacheDto> cached = redisRepository.findByTokenHash(tokenHash);
        if (cached.isPresent() && cached.get().isActive()) {
            return Optional.of(cached.get().toEntity());
        }

        Optional<RefreshToken> token = delegate.findByTokenHash(tokenHash);
        token.ifPresent(refreshToken -> {
            if (refreshToken.isActive()) {
                redisRepository.save(RefreshTokenCacheDto.from(refreshToken));
            }
        });
        return token;
    }

    @Override
    public void revokeByTokenHash(String tokenHash) {
        delegate.revokeByTokenHash(tokenHash);
        afterCommitExecutor.run(() -> redisRepository.deleteByTokenHash(tokenHash));
    }

    @Override
    public Optional<RefreshToken> findByTokenHashIncludingRevoked(String tokenHash) {
        return delegate.findByTokenHashIncludingRevoked(tokenHash);
    }

    @Override
    public void revokeAllActiveByUserId(Long userId) {
        delegate.revokeAllActiveByUserId(userId);
    }
}
