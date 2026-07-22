package com.nextbuy.passport.controller.v1;

import com.nextbuy.passport.common.advice.exception.BusinessException;
import com.nextbuy.passport.common.advice.model.ApiResponse;
import com.nextbuy.passport.configuration.JwtFilter;
import com.nextbuy.passport.controller.v1.dto.RegisterUserDto;
import com.nextbuy.passport.dto.AuthTokenResponseDto;
import com.nextbuy.passport.dto.LoginRequestDto;
import com.nextbuy.passport.dto.RefreshTokenRequestDto;
import com.nextbuy.passport.service.*;
import com.nextbuy.passport.utils.RefreshTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final RegisterService registerService;
    private final RefreshTokenUtils refreshTokenUtils;
    private final RefreshAccessTokenService refreshAccessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final ProfileService profileService;

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponseDto> login(@Valid @RequestBody LoginRequestDto request,
                                                   HttpServletRequest httpRequest) {
        var result = loginService.execute(request, refreshTokenUtils.buildContext(httpRequest));
        return ApiResponse.success("User did login successfully", result);
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterUserDto request,
                                   HttpServletRequest httpRequest) {
        var response = registerService.execute(request, refreshTokenUtils.buildContext(httpRequest));
        return ApiResponse.success("User did registered successfully", response);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request,
                                       HttpServletRequest httpRequest) {
        var response = refreshAccessTokenService.execute(request, refreshTokenUtils.buildContext(httpRequest));
        return ApiResponse.success("Refresh token issued successfully.", response);
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        refreshTokenService.revoke(request.refreshToken());
        return ApiResponse.success("Logged out successfully", null);
    }

    @GetMapping("/profile")
    public ApiResponse<?> profile(@AuthenticationPrincipal JwtFilter.AuthPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(
                    "Authentication required",
                    "AUTHENTICATION_REQUIRED",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }
        return ApiResponse.success(
                "User profile fetch successfully.", profileService.execute(principal.email())
        );
    }
}
