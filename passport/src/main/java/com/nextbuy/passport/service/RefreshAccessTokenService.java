package com.nextbuy.passport.service;


import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.AuthTokenResponseDto;
import com.nextbuy.passport.dto.GenerateJwtTokenDto;
import com.nextbuy.passport.dto.RefreshTokenContextDto;
import com.nextbuy.passport.dto.RefreshTokenRequestDto;
import com.nextbuy.passport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshAccessTokenService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Transactional
    public AuthTokenResponseDto execute(RefreshTokenRequestDto request, RefreshTokenContextDto context) {
        RefreshToken refreshToken = refreshTokenService.validateAndGet(request.refreshToken());

        User user = userRepository.findByEmail(refreshToken.getUser().getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        String accessToken = jwtService.generateAccessToken(
                new GenerateJwtTokenDto(
                        user.getId(),
                        user.getEmail(),
                        user.getAuthorities())
        );

        String newRefreshToken = refreshTokenService.rotate(
                refreshToken, user, context
        );

        return new AuthTokenResponseDto(accessToken, newRefreshToken);

    }

}
