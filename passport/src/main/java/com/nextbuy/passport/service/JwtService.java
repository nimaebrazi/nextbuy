package com.nextbuy.passport.service;

import com.nextbuy.passport.configuration.JwtConfiguration;
import com.nextbuy.passport.dto.GenerateJwtTokenDto;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtService {

    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";

    private final JwtConfiguration jwtConfiguration;

    public String generateAccessToken(GenerateJwtTokenDto req) {
        long now = System.currentTimeMillis();
        long expirationMillis = jwtConfiguration.accessTokenExpiry() * 1000L;

        return Jwts.builder()
                .subject(String.valueOf(req.userId()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMillis))
                .claims(createClaimsMap(req))
                .signWith(getSigningKey())
                .compact();
    }

    private Map<String, Object> createClaimsMap(GenerateJwtTokenDto req) {

        String rolesString = req.auths().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Map.of(
                CLAIM_EMAIL, req.email(),
                CLAIM_ROLES, rolesString
        );
    }

    public boolean isTokenValid(String token) {
        return extractToken(token).isPresent();
    }

    public Optional<JwtClaimsDto> extractToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(toJwtClaimsDto(parseClaims(token)));
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

    private JwtClaimsDto toJwtClaimsDto(Claims claims) {
        return new JwtClaimsDto(
                claims.get(CLAIM_EMAIL, String.class),
                claims.get(CLAIM_ROLES, String.class),
                Long.valueOf(claims.getSubject())
        );
    }


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfiguration.secret().getBytes(StandardCharsets.UTF_8));
    }

    public record JwtClaimsDto(
            String email,
            String roles,
            Long userId
    ) {
    }
}
