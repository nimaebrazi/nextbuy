package com.nextbuy.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtService {

    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERMISSIONS = "permissions";

    private final JwtProperties jwtProperties;

    public String generateAccessToken(GenerateAccessTokenCommand req) {
        long now = System.currentTimeMillis();
        long expirationMillis = jwtProperties.accessTokenExpiry() * 1000L;

        return Jwts.builder()
                .subject(String.valueOf(req.userId()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMillis))
                .claims(createClaimsMap(req))
                .signWith(getSigningKey())
                .compact();
    }

    private Map<String, Object> createClaimsMap(GenerateAccessTokenCommand req) {
        String rolesString = req.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(matchesRoleAuthority())
                .collect(Collectors.joining(","));
        String permissionsString = req.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(matchesRoleAuthority().negate())
                .filter(matchesAuthenticationFactorAuthority().negate())
                .collect(Collectors.joining(","));

        return Map.of(
                CLAIM_EMAIL, req.email(),
                CLAIM_ROLES, rolesString,
                CLAIM_PERMISSIONS, permissionsString
        );
    }

    public boolean isTokenValid(String token) {
        return extractToken(token).isPresent();
    }

    public Optional<JwtClaims> extractToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(toJwtClaims(parseClaims(token)));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private JwtClaims toJwtClaims(Claims claims) {
        return new JwtClaims(
                Long.valueOf(claims.getSubject()),
                claims.get(CLAIM_EMAIL, String.class),
                claims.get(CLAIM_ROLES, String.class),
                claims.get(CLAIM_PERMISSIONS, String.class)
        );
    }

    private Predicate<String> matchesRoleAuthority() {
        return authority -> authority != null && authority.startsWith("ROLE_");
    }

    /** Spring Security 7 auth-factor markers (e.g. FACTOR_PASSWORD), not business permissions. */
    private Predicate<String> matchesAuthenticationFactorAuthority() {
        return authority -> authority != null && authority.startsWith("FACTOR_");
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

}
