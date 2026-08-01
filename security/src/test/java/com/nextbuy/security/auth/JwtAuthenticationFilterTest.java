package com.nextbuy.security.auth;

import com.nextbuy.security.jwt.GenerateAccessTokenCommand;
import com.nextbuy.security.jwt.JwtProperties;
import com.nextbuy.security.jwt.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties(
                "xTmCfNhvKwErW4qixunjfNRyAts4frpY",
                30L,
                40L
        );
        jwtService = new JwtService(jwtProperties);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validBearer_setsPrincipalAndRolePermissionAuthorities() throws Exception {
        String token = jwtService.generateAccessToken(new GenerateAccessTokenCommand(
                99L,
                "mod@nextbuy.com",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("AD_APPROVE")
                )
        ));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.getPrincipal() instanceof AuthPrincipal);

        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();
        assertEquals(99L, principal.id());
        assertEquals("mod@nextbuy.com", principal.email());

        List<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("AD_APPROVE"));
    }

    @Test
    void missingBearer_leavesContextEmptyAndContinues() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void legacyTokenWithoutPermissions_claimIsStillAcceptedFromRoles() throws Exception {
        long now = System.currentTimeMillis();
        String legacyToken = Jwts.builder()
                .subject("77")
                .issuedAt(new Date(now))
                .expiration(new Date(now + (30L * 1000L)))
                .claims(Map.of(
                        JwtService.CLAIM_EMAIL, "legacy@nextbuy.com",
                        JwtService.CLAIM_ROLES, "ROLE_USER,AD_APPROVE"
                ))
                .signWith(Keys.hmacShaKeyFor("xTmCfNhvKwErW4qixunjfNRyAts4frpY".getBytes(StandardCharsets.UTF_8)))
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + legacyToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("AD_APPROVE"));
    }
}
