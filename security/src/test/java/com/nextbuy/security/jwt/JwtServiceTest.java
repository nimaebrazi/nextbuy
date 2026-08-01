package com.nextbuy.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties(
                "xTmCfNhvKwErW4qixunjfNRyAts4frpY",
                30L,
                40L
        );
        jwtService = new JwtService(jwtProperties);
    }

    @Test
    void generateAndExtractRoundTrip_returnsExpectedClaims() {
        GenerateAccessTokenCommand command = new GenerateAccessTokenCommand(
                123L,
                "user@nextbuy.com",
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("AD_APPROVE")
                )
        );

        String token = jwtService.generateAccessToken(command);
        Optional<JwtClaims> extracted = jwtService.extractToken(token);

        assertTrue(extracted.isPresent());
        JwtClaims claims = extracted.orElseThrow();
        assertEquals(123L, claims.userId());
        assertEquals("user@nextbuy.com", claims.email());
        assertEquals("ROLE_USER,ROLE_ADMIN", claims.roles());
        assertEquals("AD_APPROVE", claims.permissions());
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_withNullBlankAndInvalidTokens_returnsFalse() {
        assertFalse(jwtService.isTokenValid(null));
        assertFalse(jwtService.isTokenValid(""));
        assertFalse(jwtService.isTokenValid("   "));
        assertFalse(jwtService.isTokenValid("not-a-jwt"));
    }

    @Test
    void generateAccessToken_excludesSpringSecurityFactorAuthoritiesFromPermissions() {
        GenerateAccessTokenCommand command = new GenerateAccessTokenCommand(
                123L,
                "user@nextbuy.com",
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("FACTOR_PASSWORD"),
                        new SimpleGrantedAuthority("AD_APPROVE")
                )
        );

        String token = jwtService.generateAccessToken(command);
        JwtClaims claims = jwtService.extractToken(token).orElseThrow();

        assertEquals("ROLE_USER", claims.roles());
        assertEquals("AD_APPROVE", claims.permissions());
    }
}
