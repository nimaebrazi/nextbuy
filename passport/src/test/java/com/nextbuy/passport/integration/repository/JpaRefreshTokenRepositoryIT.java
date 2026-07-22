package com.nextbuy.passport.integration.repository;


import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.repository.RefreshTokenRepository;
import com.nextbuy.passport.repository.jpa.JpaRefreshTokenRepositoryAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Tags({@Tag("integration"), @Tag("repository")})
@Import({
        JpaRefreshTokenRepositoryAdapter.class,
        RepositoryTestData.class,
        RepositoryTestScenarios.class
})
class JpaRefreshTokenRepositoryIT extends JpaRepositoryIntegrationTest {

    private static final String TOKEN_HASH = "a".repeat(64);

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RepositoryTestScenarios scenarios;

    @Test
    void findByTokenHash_returnsPersistedTokenWithUser() {
        var scenario = scenarios.userWithActiveRefreshToken("user@example.com", TOKEN_HASH);

        assertThat(refreshTokenRepository.findByTokenHash(TOKEN_HASH))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getId()).isEqualTo(scenario.token().getId());
                    assertThat(found.getTokenHash()).isEqualTo(TOKEN_HASH);
                    assertThat(found.getIp()).isEqualTo(scenario.token().getIp());
                    assertThat(found.getUserAgent()).isEqualTo(scenario.token().getUserAgent());
                    assertThat(found.getDevice()).isEqualTo(scenario.token().getDevice());
                    assertThat(found.getRevokedAt()).isNull();
                    assertThat(found.isActive()).isTrue();
                    assertThat(found.getUser().getId()).isEqualTo(scenario.user().getId());
                    assertThat(found.getUser().getEmail()).isEqualTo(scenario.user().getEmail());
                });
    }

    @Test
    void findByTokenHash_returnsEmptyWhenTokenDoesNotExist() {
        assertThat(refreshTokenRepository.findByTokenHash("b".repeat(64))).isEmpty();
    }

    @Test
    void revokeByTokenHash_softRevokesActiveToken() {
        var scenario = scenarios.userWithActiveRefreshToken("revoke@example.com", TOKEN_HASH);

        refreshTokenRepository.revokeByTokenHash(TOKEN_HASH);

        assertThat(refreshTokenRepository.findByTokenHash(TOKEN_HASH))
                .isPresent()
                .get()
                .satisfies(token -> {
                    assertThat(token.getRevokedAt()).isNotNull();
                    assertThat(token.isActive()).isFalse();
                    assertThat(token.getUser().getId()).isEqualTo(scenario.user().getId());
                });
    }

    @Test
    void revokeByTokenHash_isNoOpWhenTokenDoesNotExist() {
        assertThatCode(() -> refreshTokenRepository.revokeByTokenHash("c".repeat(64)))
                .doesNotThrowAnyException();
    }

    @Test
    void revokeByTokenHash_doesNotUpdateAlreadyRevokedToken() {
        scenarios.userWithActiveRefreshToken("already-revoked@example.com", TOKEN_HASH);

        refreshTokenRepository.revokeByTokenHash(TOKEN_HASH);
        var firstRevokedAt = refreshTokenRepository.findByTokenHash(TOKEN_HASH)
                .orElseThrow()
                .getRevokedAt();

        refreshTokenRepository.revokeByTokenHash(TOKEN_HASH);

        assertThat(refreshTokenRepository.findByTokenHash(TOKEN_HASH))
                .isPresent()
                .get()
                .extracting(RefreshToken::getRevokedAt)
                .isEqualTo(firstRevokedAt);
    }
}
