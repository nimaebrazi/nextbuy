package com.nextbuy.passport.service;


import com.nextbuy.passport.configuration.JwtConfiguration;
import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.RefreshTokenContextDto;
import com.nextbuy.passport.exceptions.AuthExceptions;
import com.nextbuy.passport.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfiguration jwtConfiguration;

    @Transactional
    public String create(User user, RefreshTokenContextDto context) {

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = sha256(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(jwtConfiguration.refreshTokenExpiry()))
                .ip(context.ip())
                .userAgent(context.userAgent())
                .device(context.device())
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    public RefreshToken validateAndGet(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw AuthExceptions.refreshTokenRequired();
        }

        String tokenHash = sha256(rawRefreshToken);
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashIncludingRevoked(tokenHash)
                .orElseThrow(AuthExceptions::invalidRefreshToken);

        if (refreshToken.isRevoked()) {
            refreshTokenRepository.revokeAllActiveByUserId(refreshToken.getUser().getId());
            throw AuthExceptions.refreshTokenReuseDetected();
        }

        if (refreshToken.isExpired()) {
            throw AuthExceptions.invalidRefreshToken();
        }
        return refreshToken;
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }
        refreshTokenRepository.revokeByTokenHash(sha256(rawRefreshToken));
    }

    @Transactional
    public void revoke(RefreshToken token) {
        if (token == null || !token.isRevoked()) {
            return;
        }
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
    }

    @Transactional
    public String rotate(RefreshToken token, User user, RefreshTokenContextDto context) {
        revoke(token);
        return create(user, context);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
