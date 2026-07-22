package com.nextbuy.passport.unit.service;

import com.nextbuy.passport.configuration.JwtConfiguration;
import com.nextbuy.passport.dto.GenerateJwtTokenDto;
import com.nextbuy.passport.service.JwtService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tags({@Tag("unit"), @Tag("service")})
@DisplayName("JwtUtilTests")
public class JwtServiceTest {

    private final static String longSecret = "mySuperSecretKeyThatIsAtLeast32CharactersLong1234";
    private final static long accessTokenExpiry = 3600L;

    private final JwtService jwtService = new JwtService(
            new JwtConfiguration(longSecret, accessTokenExpiry, 0L)
    );

    @Test
    @DisplayName("It should generate jwt token.")
    public void test_GenerateJwt_GivenGenerateJwtDto_WhenRequestIsCorrect_ThenGenerateToken() {
        GenerateJwtTokenDto req = new GenerateJwtTokenDto(1L, "nima@example.com", Collections.emptySet());

        String token = jwtService.generateAccessToken(req);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("It should return false when token is null.")
    public void test_IsTokenValid_WhenTokenIsNull_ThenReturnFalse() {
        assertThat(jwtService.isTokenValid(null)).isFalse();
    }

    @Test
    @DisplayName("It should return false when token is blank.")
    public void test_IsTokenValid_WhenTokenIsBlank_ThenReturnFalse() {
        assertThat(jwtService.isTokenValid("")).isFalse();
        assertThat(jwtService.isTokenValid("    ")).isFalse();
    }

    @Test
    @DisplayName("It should return false when token is blank.")
    public void test_IsTokenValid_WhenParseTokenThrowsException_ThenReturnFalse() {
        assertThat(jwtService.isTokenValid("invalid-token")).isFalse();
    }

    @Test
    @DisplayName("It should extract token claims.")
    public void test_ExtractToken_WhenTokenIsValid_ThenReturnClaims() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        String rolesString = "ROLE_USER,ROLE_ADMIN";
        String email = "nima@example.com";

        GenerateJwtTokenDto req = new GenerateJwtTokenDto(1L, email, authorities);

        String token = jwtService.generateAccessToken(req);
        JwtService.JwtClaimsDto claims = jwtService.extractToken(token).orElseThrow();

        assertThat(claims.userId()).isEqualTo(1L);
        assertThat(claims.email()).isEqualTo(email);
        assertThat(claims.roles()).isEqualTo(rolesString);
    }

    @Test
    @DisplayName("It should return empty when extracting invalid token.")
    public void test_ExtractToken_WhenTokenIsInvalid_ThenReturnEmpty() {
        assertThat(jwtService.extractToken("invalid-token")).isEmpty();
    }
}
