package com.nextbuy.passport.controller.v1;

import com.nextbuy.passport.common.advice.exception.BusinessException;
import com.nextbuy.passport.common.advice.model.ApiResponse;
import com.nextbuy.passport.controller.v1.dto.RegisterUserDto;
import com.nextbuy.passport.dto.AuthTokenResponseDto;
import com.nextbuy.passport.dto.LoginRequestDto;
import com.nextbuy.passport.dto.RefreshTokenRequestDto;
import com.nextbuy.passport.service.LoginService;
import com.nextbuy.passport.service.ProfileService;
import com.nextbuy.passport.service.RefreshAccessTokenService;
import com.nextbuy.passport.service.RefreshTokenService;
import com.nextbuy.passport.service.RegisterService;
import com.nextbuy.passport.utils.RefreshTokenUtils;
import com.nextbuy.security.auth.AuthPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResponse<?> profile(@AuthenticationPrincipal AuthPrincipal principal) {
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
