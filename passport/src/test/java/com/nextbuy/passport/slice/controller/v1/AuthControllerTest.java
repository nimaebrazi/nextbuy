package com.nextbuy.passport.slice.controller.v1;


import com.nextbuy.passport.configuration.JwtFilter;
import com.nextbuy.passport.configuration.RateLimitFilter;
import com.nextbuy.passport.controller.v1.AuthController;
import com.nextbuy.passport.controller.v1.dto.RegisterUserDto;
import com.nextbuy.passport.dto.AuthTokenResponseDto;
import com.nextbuy.passport.dto.RefreshTokenContextDto;
import com.nextbuy.passport.dto.RefreshTokenRequestDto;
import com.nextbuy.passport.dto.UserProfileDto;
import com.nextbuy.passport.service.*;
import com.nextbuy.passport.support.controller.ControllerTestBase;
import com.nextbuy.passport.support.controller.JsonPaths;
import com.nextbuy.passport.support.fixtures.AuthContexts;
import com.nextbuy.passport.support.fixtures.AuthTokens;
import com.nextbuy.passport.support.fixtures.LoginRequests;
import com.nextbuy.passport.utils.RefreshTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@Tags({@Tag("slice"), @Tag("controller")})
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest extends ControllerTestBase {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private RegisterService registerService;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private RefreshTokenUtils refreshTokenUtils;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private RefreshAccessTokenService refreshAccessTokenService;


    @Test
    @DisplayName("it should login user successfully")
    void login_ItShouldLoginUserAndResponseForAccessAndRefreshToken() throws Exception {
        var request = LoginRequests.withCredentials("nima@gmail.com", "my-password");
        var context = AuthContexts.refreshTokenContext();

        var response = AuthTokens.response();

        given(refreshTokenUtils.buildContext(any(HttpServletRequest.class))).willReturn(context);
        given(loginService.execute(eq(request), eq(context))).willReturn(response);


        mockMvcUtils.performPost("/api/v1/login", request)
                .andExpect(JsonPaths.statusOk())
                .andExpectAll(JsonPaths.apiStructure())
                .andExpect(JsonPaths.isSuccess())
                .andExpect(JsonPaths.path("/api/v1/login"))
                .andExpect(JsonPaths.message("User did login successfully"))
                .andExpect(JsonPaths.accessToken(response.accessToken()))
                .andExpect(JsonPaths.refreshToken(response.refreshToken()));
    }


    @Test
    @DisplayName("it should register user successfully")
    void register_ItShouldRegisterUserAndResponseForAccessAndRefreshToken() throws Exception {
        var request = new RegisterUserDto("nima@gmail.com", "my-password");
        var context = new RefreshTokenContextDto(
                "127.0.0.1",
                "test-agent",
                "Device:Other;OS:Other 0.0"
        );
        var response = new AuthTokenResponseDto(
                "my-access-token",
                "my-refresh-token"
        );

        given(refreshTokenUtils.buildContext(any(HttpServletRequest.class))).willReturn(context);
        given(registerService.execute(eq(request), eq(context))).willReturn(response);


        mockMvcUtils.performPost("/api/v1/register", request)
                .andExpect(JsonPaths.statusOk())
                .andExpectAll(JsonPaths.apiStructure())
                .andExpect(JsonPaths.path("/api/v1/register"))
                .andExpect(JsonPaths.isSuccess())
                .andExpect(JsonPaths.message("User did registered successfully"))
                .andExpect(JsonPaths.accessToken("my-access-token"))
                .andExpect(JsonPaths.refreshToken("my-refresh-token"));

        verify(registerService).execute(eq(request), eq(context));
        verify(refreshTokenUtils).buildContext(any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("it should issue new refresh token by old refresh token.")
    void refreshToken_ItShouldRefreshTokenAndResponseForAccessAndRefreshToken() throws Exception {
        var request = new RefreshTokenRequestDto("my-refresh-token");

        var context = new RefreshTokenContextDto(
                "127.0.0.1",
                "test-agent",
                "Device:Other;OS:Other 0.0"
        );
        var response = new AuthTokenResponseDto(
                "my-access-token",
                "my-refresh-token"
        );


        given(refreshTokenUtils.buildContext(any(HttpServletRequest.class))).willReturn(context);
        given(refreshAccessTokenService.execute(request, context)).willReturn(response);


        mockMvcUtils.performPost("/api/v1/refresh-token", request).
                andExpect(JsonPaths.statusOk())
                .andExpectAll(JsonPaths.apiStructure())
                .andExpect(JsonPaths.path("/api/v1/refresh-token"))
                .andExpect(JsonPaths.isSuccess())
                .andExpect(JsonPaths.message("Refresh token issued successfully."))
                .andExpect(JsonPaths.accessToken("my-access-token"))
                .andExpect(JsonPaths.refreshToken("my-refresh-token"));

        verify(refreshTokenUtils).buildContext(any(HttpServletRequest.class));
        verify(refreshAccessTokenService).execute(request, context);
    }

    @Test
    @DisplayName("It should logout user.")
    void logout_ItShouldLogoutUser() throws Exception {
        var request = new RefreshTokenRequestDto("my-refresh-token");

        mockMvcUtils.performPost("/api/v1/logout", request)
                .andExpect(JsonPaths.statusOk())
                .andExpectAll(JsonPaths.apiStructure())
                .andExpect(JsonPaths.path("/api/v1/logout"))
                .andExpect(JsonPaths.isSuccess())
                .andExpect(JsonPaths.message("Logged out successfully"));

        verify(refreshTokenService).revoke("my-refresh-token");
    }

    @Test
    @DisplayName("it should reject blank refresh token on logout")
    void logout_WhenRefreshTokenIsBlank_ShouldReturnValidationError() throws Exception {
        var request = new RefreshTokenRequestDto("");

        mockMvcUtils.performPost("/api/v1/logout", request)
                .andExpect(JsonPaths.statusBadRequest())
                .andExpectAll(JsonPaths.apiStructure())
                .andExpect(JsonPaths.isNotSuccess())
                .andExpect(JsonPaths.path("/api/v1/logout"));

        verify(refreshTokenService, never()).revoke(anyString());
    }

    @Test
    @DisplayName("it should reject profile request when authentication principal is missing")
    void profile_WhenPrincipalIsMissing_ShouldReturnAuthenticationRequired() throws Exception {
        mockMvcUtils.performGet("/api/v1/profile")
                .andExpect(JsonPaths.statusUnauthorized())
                .andExpectAll(JsonPaths.apiStructure())
                .andExpect(JsonPaths.path("/api/v1/profile"))
                .andExpect(JsonPaths.isNotSuccess())
                .andExpect(JsonPaths.message("Authentication required"))
                .andExpect(JsonPaths.errorCode("AUTHENTICATION_REQUIRED"));
    }

    @Test
    @DisplayName("it should show user profile for authenticated profile request")
    void profile_WhenPrincipalExists_ShouldShowUserProfile() throws Exception {
        var principal = new JwtFilter.AuthPrincipal(1L, "nima@gmail.com");
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of());
        var response = new UserProfileDto(1L, "nima@gmail.com");

        given(profileService.execute(principal.email())).willReturn(response);

        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            mockMvcUtils.performGet("/api/v1/profile")
                    .andExpect(JsonPaths.statusOk())
                    .andExpectAll(JsonPaths.apiStructure())
                    .andExpect(JsonPaths.path("/api/v1/profile"))
                    .andExpect(JsonPaths.isSuccess())
                    .andExpect(JsonPaths.message("User profile fetch successfully."))
                    .andExpect(JsonPaths.profileId(1L))
                    .andExpect(JsonPaths.profileEmail("nima@gmail.com"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(profileService).execute(principal.email());
    }
}
