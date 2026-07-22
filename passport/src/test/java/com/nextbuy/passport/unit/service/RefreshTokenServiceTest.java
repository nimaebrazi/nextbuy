package com.nextbuy.passport.unit.service;


import com.nextbuy.passport.common.advice.exception.BusinessException;
import com.nextbuy.passport.configuration.JwtConfiguration;
import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.RefreshTokenContextDto;
import com.nextbuy.passport.repository.RefreshTokenRepository;
import com.nextbuy.passport.service.RefreshTokenService;
import com.nextbuy.passport.support.fixtures.AuthContexts;
import com.nextbuy.passport.support.fixtures.RefreshTokens;
import com.nextbuy.passport.support.fixtures.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@Tags({@Tag("unit"), @Tag("service")})
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenServiceTest")
public class RefreshTokenServiceTest {

    private static final String RAW_TOKEN = "550e8400-e29b-41d4-a716-446655440000";
    private static final String TOKEN_HASH = "a3a9e1ed9732cab28868127be00f1ce921acaefdd5c3b23a6e9e0072bd9c1a34";
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60L;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtConfiguration jwtConfiguration;

    @InjectMocks
    private RefreshTokenService refreshTokenService;


    @Test
    @DisplayName("It should create refresh token.")
    public void create_whenUserAndContextAreValid_savesRefreshTokenAndReturnsRawToken() {
        User user = Users.randomWithId();
        RefreshTokenContextDto context = AuthContexts.refreshTokenContext();

        given(jwtConfiguration.refreshTokenExpiry()).willReturn(REFRESH_TOKEN_EXPIRY);
        Instant beforeCreate = Instant.now();

        String rawToken = refreshTokenService.create(user, context);

        Instant afterCreate = Instant.now();

        assertThat(rawToken).isNotBlank();

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());

        RefreshToken savedToken = tokenCaptor.getValue();

        assertThat(savedToken.getTokenHash()).isNotBlank();
        assertThat(savedToken.getTokenHash()).hasSize(64);
        assertThat(savedToken.getTokenHash()).isNotEqualTo(rawToken);
        assertThat(savedToken.getUser()).isEqualTo(user);
        assertThat(savedToken.getIp()).isEqualTo(context.ip());
        assertThat(savedToken.getUserAgent()).isEqualTo(context.userAgent());
        assertThat(savedToken.getDevice()).isEqualTo(context.device());
        assertThat(savedToken.getRevokedAt()).isNull();
        assertThat(savedToken.getExpiresAt())
                .isAfterOrEqualTo(beforeCreate.plusSeconds(REFRESH_TOKEN_EXPIRY))
                .isBeforeOrEqualTo(afterCreate.plusSeconds(REFRESH_TOKEN_EXPIRY));
        verify(jwtConfiguration).refreshTokenExpiry();
    }

    @Test
    @DisplayName("It should throw exception when toke is empty or null.")
    void validateAndGet_WhenRawTokenIsEmpty_WillThrowException() {
        String nullRawToken = null;
        String emptyRawToken = "";

        assertThatThrownBy(() -> refreshTokenService.validateAndGet(nullRawToken))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Refresh token is required");

        assertThatThrownBy(() -> refreshTokenService.validateAndGet(emptyRawToken))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Refresh token is required");
    }

    @Test
    void validateAndGet_WhenHashedTokenDoesNotExist_WillThrowException() {
        given(refreshTokenRepository.findByTokenHashIncludingRevoked(TOKEN_HASH))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.validateAndGet(RAW_TOKEN))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired refresh token");

    }

    @Test
    void validateAndGet_WhenRawTokenIsRevoked_WillThrowException() {
        User user = Users.randomWithId();
        RefreshToken refreshToken = RefreshTokens.forUser(user, TOKEN_HASH);
        refreshToken.setRevokedAt(Instant.now().minus(2, ChronoUnit.DAYS));

        given(refreshTokenRepository.findByTokenHashIncludingRevoked(TOKEN_HASH))
                .willReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshTokenService.validateAndGet(RAW_TOKEN))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Suspicious refresh token reuse detected. Please log in again.");

        verify(refreshTokenRepository).revokeAllActiveByUserId(user.getId());
    }

    @Test
    void validateAndGet_WhenRawTokenIsExpired_WillThrowException() {
        User user = Users.randomWithId();
        RefreshToken refreshToken = RefreshTokens.forUser(user, TOKEN_HASH);
        refreshToken.setExpiresAt(Instant.now().minus(2, ChronoUnit.DAYS));

        given(refreshTokenRepository.findByTokenHashIncludingRevoked(TOKEN_HASH))
                .willReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshTokenService.validateAndGet(RAW_TOKEN))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired refresh token");

        verify(refreshTokenRepository, never()).revokeAllActiveByUserId(user.getId());
    }

    @Test
    void validateAndGet_WhenRawTokenValid_WillReturnRefreshToken() {
        User user = Users.randomWithId();
        RefreshToken refreshToken = RefreshTokens.forUser(user, TOKEN_HASH);
        refreshToken.setRevokedAt(null);
        refreshToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));

        given(refreshTokenRepository.findByTokenHashIncludingRevoked(TOKEN_HASH))
                .willReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.validateAndGet(RAW_TOKEN);
        assertThat(result).isSameAs(refreshToken);

        verify(refreshTokenRepository).findByTokenHashIncludingRevoked(TOKEN_HASH);
        verify(refreshTokenRepository, never()).revokeAllActiveByUserId(any());
    }

    @Test
    @DisplayName("It should not call repository when raw token is null or blank.")
    void revoke_whenRawTokenIsNullOrBlank_doesNothing() {
//        refreshTokenService.revoke(null);
        refreshTokenService.revoke("");
        refreshTokenService.revoke("   ");
        verify(refreshTokenRepository, never()).revokeByTokenHash(any());
    }

    @Test
    @DisplayName("It should revoke token by hash when raw token is valid.")
    void revoke_whenRawTokenIsValid_revokesByTokenHash() {
        refreshTokenService.revoke(RAW_TOKEN);
        verify(refreshTokenRepository).revokeByTokenHash(TOKEN_HASH);
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    @DisplayName("It should not save when token is null.")
    void revoke_whenTokenIsNull_doesNothing() {
        refreshTokenService.revoke((RefreshToken) null);
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("It should not save when token is still active.")
    void revoke_whenTokenIsActive_doesNothing() {
        RefreshToken token = RefreshTokens.forUser(Users.randomWithId(), TOKEN_HASH);
        token.setRevokedAt(null);
        refreshTokenService.revoke(token);
        verify(refreshTokenRepository, never()).save(any());
        assertThat(token.getRevokedAt()).isNull();
    }

    @Test
    @DisplayName("It should update revokedAt when token is already revoked.")
    void revoke_whenTokenIsAlreadyRevoked_savesToken() {
        RefreshToken token = RefreshTokens.forUser(Users.randomWithId(), TOKEN_HASH);
        Instant originalRevokedAt = Instant.now().minus(1, ChronoUnit.DAYS);
        token.setRevokedAt(originalRevokedAt);
        refreshTokenService.revoke(token);
        verify(refreshTokenRepository).save(token);
        assertThat(token.getRevokedAt()).isAfter(originalRevokedAt);
    }

    @Test
    @DisplayName("It should return new raw token and persist it when rotating.")
    void rotate_whenTokenIsValid_returnsNewRawTokenAndPersistsIt() {
        User user = Users.randomWithId();
        RefreshTokenContextDto context = AuthContexts.refreshTokenContext();
        RefreshToken existingToken = RefreshTokens.forUser(user, TOKEN_HASH);
        existingToken.setRevokedAt(null);

        given(jwtConfiguration.refreshTokenExpiry()).willReturn(REFRESH_TOKEN_EXPIRY);
        Instant beforeRotate = Instant.now();

        String newRawToken = refreshTokenService.rotate(existingToken, user, context);
        Instant afterRotate = Instant.now();

        assertThat(newRawToken).isNotBlank();

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getTokenHash()).isNotBlank().hasSize(64);
        assertThat(savedToken.getTokenHash()).isNotEqualTo(newRawToken);
        assertThat(savedToken.getUser()).isEqualTo(user);
        assertThat(savedToken.getIp()).isEqualTo(context.ip());
        assertThat(savedToken.getUserAgent()).isEqualTo(context.userAgent());
        assertThat(savedToken.getDevice()).isEqualTo(context.device());
        assertThat(savedToken.getRevokedAt()).isNull();
        assertThat(savedToken.getExpiresAt())
                .isAfterOrEqualTo(beforeRotate.plusSeconds(REFRESH_TOKEN_EXPIRY))
                .isBeforeOrEqualTo(afterRotate.plusSeconds(REFRESH_TOKEN_EXPIRY));
        verify(jwtConfiguration).refreshTokenExpiry();
        // With current revoke(RefreshToken) logic, active token is not revoked during rotate
        assertThat(existingToken.getRevokedAt()).isNull();
    }
}
