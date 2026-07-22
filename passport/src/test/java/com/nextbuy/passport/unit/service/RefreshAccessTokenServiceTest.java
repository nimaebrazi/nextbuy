package com.nextbuy.passport.unit.service;


import com.nextbuy.passport.common.advice.exception.BusinessException;
import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.AuthTokenResponseDto;
import com.nextbuy.passport.dto.GenerateJwtTokenDto;
import com.nextbuy.passport.dto.RefreshTokenContextDto;
import com.nextbuy.passport.dto.RefreshTokenRequestDto;
import com.nextbuy.passport.exceptions.AuthExceptions;
import com.nextbuy.passport.repository.UserRepository;
import com.nextbuy.passport.service.JwtService;
import com.nextbuy.passport.service.RefreshAccessTokenService;
import com.nextbuy.passport.service.RefreshTokenService;
import com.nextbuy.passport.support.fixtures.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@Tags({@Tag("unit"), @Tag("service")})
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshAccessTokenServiceTest")
public class RefreshAccessTokenServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshAccessTokenService refreshAccessTokenService;

    @Test
    @DisplayName("It should throw when user email from refresh token is not found")
    void execute_WhenEmailDoesNotExist_WillThrowException() {
        RefreshTokenRequestDto request = RefreshTokenRequests.withToken("token");
        RefreshTokenContextDto context = AuthContexts.refreshTokenContext();
        User user = Users.randomWithId();
        RefreshToken refreshToken = RefreshTokens.forUser(user, "hash-token");
        String emailFromToken = refreshToken.getUser().getEmail();

        given(refreshTokenService.validateAndGet(request.refreshToken())).willReturn(refreshToken);
        given(userRepository.findByEmail(emailFromToken)).willReturn(Optional.empty());

        assertThatThrownBy(() -> refreshAccessTokenService.execute(request, context))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found.");

        verify(refreshTokenService).validateAndGet(request.refreshToken());
        verify(userRepository).findByEmail(emailFromToken);
        verify(jwtService, never()).generateAccessToken(any(GenerateJwtTokenDto.class));
        verify(refreshTokenService, never()).rotate(any(RefreshToken.class), any(User.class), any(RefreshTokenContextDto.class));
        verifyNoMoreInteractions(jwtService, refreshTokenService, userRepository);
    }

    @Test
    @DisplayName("It should propagate when refresh token validation fails")
    void execute_WhenTokenIsInvalid_WillThrowBusinessException() {
        RefreshTokenRequestDto request = RefreshTokenRequests.withToken("bad-token");
        RefreshTokenContextDto context = AuthContexts.refreshTokenContext();

        given(refreshTokenService.validateAndGet(request.refreshToken()))
                .willThrow(AuthExceptions.invalidRefreshToken());

        assertThatThrownBy(() -> refreshAccessTokenService.execute(request, context))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired refresh token")
                .extracting("errorCode")
                .isEqualTo("INVALID_REFRESH_TOKEN");

        verify(refreshTokenService).validateAndGet(request.refreshToken());
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateAccessToken(any(GenerateJwtTokenDto.class));
        verify(refreshTokenService, never()).rotate(any(RefreshToken.class), any(User.class), any(RefreshTokenContextDto.class));
        verifyNoMoreInteractions(jwtService, refreshTokenService, userRepository);
    }

    @Test
    @DisplayName("It should propagate when refresh token reuse is detected")
    void execute_WhenTokenReuseDetected_WillThrowBusinessException() {
        RefreshTokenRequestDto request = RefreshTokenRequests.withToken("reused-token");
        RefreshTokenContextDto context = AuthContexts.refreshTokenContext();

        given(refreshTokenService.validateAndGet(request.refreshToken()))
                .willThrow(AuthExceptions.refreshTokenReuseDetected());

        assertThatThrownBy(() -> refreshAccessTokenService.execute(request, context))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Suspicious refresh token reuse detected. Please log in again.")
                .extracting("errorCode")
                .isEqualTo("REFRESH_TOKEN_REUSE");

        verify(refreshTokenService).validateAndGet(request.refreshToken());
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateAccessToken(any(GenerateJwtTokenDto.class));
        verify(refreshTokenService, never()).rotate(any(RefreshToken.class), any(User.class), any(RefreshTokenContextDto.class));
        verifyNoMoreInteractions(jwtService, refreshTokenService, userRepository);
    }


    @Test
    @DisplayName("It should return new access and refresh tokens when refresh token is valid.")
    void execute_WhenTokenExists_WillReturnRefreshToken() {
        RefreshTokenRequestDto request = RefreshTokenRequests.withToken("token");
        RefreshTokenContextDto context = AuthContexts.refreshTokenContext();
        User user = Users.randomWithId();
        RefreshToken refreshToken = RefreshTokens.forUser(user, "hash-token");
        String accessToken = AuthTokens.accessToken();
        String newRefreshToken = AuthTokens.refreshToken();
        String emailFromToken = refreshToken.getUser().getEmail();

        given(refreshTokenService.validateAndGet(request.refreshToken())).willReturn(refreshToken);
        given(userRepository.findByEmail(emailFromToken)).willReturn(Optional.of(user));
        given(jwtService.generateAccessToken(any(GenerateJwtTokenDto.class))).willReturn(accessToken);
        given(refreshTokenService.rotate(refreshToken, user, context)).willReturn(newRefreshToken);

        AuthTokenResponseDto response = refreshAccessTokenService.execute(request, context);

        assertThat(response).isEqualTo(new AuthTokenResponseDto(accessToken, newRefreshToken));

        verify(refreshTokenService).validateAndGet(request.refreshToken());
        verify(userRepository).findByEmail(emailFromToken);
        verify(refreshTokenService).rotate(refreshToken, user, context);


        ArgumentCaptor<GenerateJwtTokenDto> jwtCaptor = ArgumentCaptor.forClass(GenerateJwtTokenDto.class);
        verify(jwtService).generateAccessToken(jwtCaptor.capture());
        GenerateJwtTokenDto jwtRequest = jwtCaptor.getValue();
        assertThat(jwtRequest.userId()).isEqualTo(user.getId());
        assertThat(jwtRequest.email()).isEqualTo(user.getEmail());

        verifyNoMoreInteractions(jwtService, refreshTokenService, userRepository);
    }

}
