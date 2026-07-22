package com.nextbuy.passport.service;


import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.*;
import com.nextbuy.passport.exceptions.AuthExceptions;
import com.nextbuy.passport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RateLimitService rateLimitService;

    public AuthTokenResponseDto execute(LoginRequestDto request, RefreshTokenContextDto context) {

        RateLimitResult userResult = rateLimitService.checkUser(request.email());
        if (!userResult.allowed()) {
            throw AuthExceptions.tooManyRequests(userResult.retryAfterSeconds());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email Not Found"));

        var accessToken = jwtService.generateAccessToken(
                new GenerateJwtTokenDto(
                        user.getId(),
                        user.getEmail(),
                        authentication.getAuthorities()
                )
        );

        var refreshToken = refreshTokenService.create(user, context);

        return new AuthTokenResponseDto(accessToken, refreshToken);
    }
}
