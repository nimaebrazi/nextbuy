package com.nextbuy.passport.unit.service;


import com.nextbuy.passport.common.advice.exception.BusinessException;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.*;
import com.nextbuy.passport.repository.UserRepository;
import com.nextbuy.passport.service.JwtService;
import com.nextbuy.passport.service.LoginService;
import com.nextbuy.passport.service.RateLimitService;
import com.nextbuy.passport.service.RefreshTokenService;
import com.nextbuy.passport.support.fixtures.*;
import com.nextbuy.passport.support.utils.Fakers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tags({@Tag("unit"), @Tag("service")})
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginServiceTests")
public class LoginServiceTest {

    @Mock
    JwtService jwtService;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    RateLimitService rateLimitService;

    @Mock
    Authentication authentication;

    @InjectMocks
    LoginService loginService;


    @Test
    @DisplayName("IT should throw exception when email doesn't exist.")
    public void execute_whenEmailDoesNotExist_thenThrowException() {
        LoginRequestDto req = LoginRequests.random();
        RefreshTokenContextDto refreshTokenContext = AuthContexts.refreshTokenContext();
        RateLimitResult allowedRateLimiterResult = RateLimitResults.allowed(10);

        given(rateLimitService.checkUser(req.email())).willReturn(allowedRateLimiterResult);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(userRepository.findByEmail(req.email())).willReturn(Optional.empty());


        assertThatThrownBy(() -> loginService.execute(req, refreshTokenContext))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Email Not Found");


        verify(rateLimitService).checkUser(req.email());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(req.email());
    }

    @Test
    @DisplayName("It should return tokens when credentials are valid.")
    public void execute_whenCredentialsAreValid_thenReturnTokens() {
        LoginRequestDto req = LoginRequests.random();
        User user = Users.withIdAndEmail(req.email(), Fakers.randomPassword());
        RefreshTokenContextDto refreshTokenContext = AuthContexts.refreshTokenContext();
        String accessToken = AuthTokens.accessToken();
        String refreshToken = AuthTokens.refreshToken();
        Set<GrantedAuthority> authorities = Authorities.of("ROLE_USER");
        RateLimitResult alloweRateLimitResult = RateLimitResults.allowed(10);

        given(rateLimitService.checkUser(req.email())).willReturn(alloweRateLimitResult);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        doReturn(authorities).when(authentication).getAuthorities();
        given(userRepository.findByEmail(req.email())).willReturn(Optional.of(user));
        given(jwtService.generateAccessToken(any(GenerateJwtTokenDto.class))).willReturn(accessToken);
        given(refreshTokenService.create(eq(user), any(RefreshTokenContextDto.class))).willReturn(refreshToken);

        AuthTokenResponseDto response = loginService.execute(req, refreshTokenContext);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(refreshToken);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(req.email());
        verify(jwtService).generateAccessToken(any(GenerateJwtTokenDto.class));
        verify(refreshTokenService).create(eq(user), any(RefreshTokenContextDto.class));
    }

    @Test
    @DisplayName("It should return tokens when credentials are valid.")
    public void execute_whenUserTooManyAttempts_thenThrowException() {
        LoginRequestDto req = LoginRequests.random();
        RefreshTokenContextDto refreshTokenContext = AuthContexts.refreshTokenContext();
        RateLimitResult blockedRateLimitResult = RateLimitResults.blocked(10);

        given(rateLimitService.checkUser(req.email())).willReturn(blockedRateLimitResult);

        assertThatThrownBy(() -> loginService.execute(req, refreshTokenContext))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Too many attempts. Try again in " + blockedRateLimitResult.retryAfterSeconds() + " seconds.");

        verify(rateLimitService).checkUser(req.email());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
