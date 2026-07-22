package com.nextbuy.passport.unit.service;


import com.nextbuy.passport.common.advice.exception.BusinessException;
import com.nextbuy.passport.controller.v1.dto.RegisterUserDto;
import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.AuthTokenResponseDto;
import com.nextbuy.passport.dto.LoginRequestDto;
import com.nextbuy.passport.dto.RefreshTokenContextDto;
import com.nextbuy.passport.repository.UserRepository;
import com.nextbuy.passport.service.LoginService;
import com.nextbuy.passport.service.RegisterService;
import com.nextbuy.passport.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@Tags({@Tag("unit"), @Tag("service")})
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterService Tests")
public class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginService loginService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterService registerService;


    private static final String EMAIL = "nima@example.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String IP = "127.0.0.1";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String DEVICE = "Device:Other;OS:Android 14.0";

    @Test
    @DisplayName("It should throw exception when user exist.")
    public void register_whenEmailExists_throwException() {

        RegisterUserDto registerUserDto = registerRequest();
        RefreshTokenContextDto context = refreshTokenContext();

        given(userRepository.existsByEmail(EMAIL)).willReturn(true);

        assertThatThrownBy(() ->
                registerService.execute(registerUserDto, context))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email already exists")
                .extracting("errorCode").isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("It throws exception when default role does not exist.")
    void register_whenDefaultRoleDoesNotExist_throwsException() {
        RegisterUserDto registerUserDto = registerRequest();
        RefreshTokenContextDto context = refreshTokenContext();

        given(userRepository.existsByEmail(EMAIL)).willReturn(false);
        given(roleService.getByName(ROLE_USER)).willReturn(null);
        assertThatThrownBy(() ->
                registerService.execute(registerUserDto, context))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Role not found");

        verify(userRepository).existsByEmail(EMAIL);
        verify(roleService).getByName(ROLE_USER);
    }

    @Test
    @DisplayName("It register user when email doesn't exist.")
    public void register_whenEmailDoesNotExist_savesUserAndReturnsTokens() {

        // given
        RegisterUserDto registerUserDto = registerRequest();
        Role role = userRole();
        AuthTokenResponseDto authToken = authToken();
        LoginRequestDto loginRequestDto = loginRequest();
        RefreshTokenContextDto context = refreshTokenContext();

        given(userRepository.existsByEmail(registerUserDto.email())).willReturn(false);
        given(roleService.getByName(ROLE_USER)).willReturn(role);
        given(passwordEncoder.encode(registerUserDto.password())).willReturn(ENCODED_PASSWORD);
        given(userRepository.save(any(User.class))).willReturn(savedUser());
        given(loginService.execute(loginRequestDto, context)).willReturn(authToken);

        // when
        AuthTokenResponseDto response = registerService.execute(registerUserDto, context);

        // then
        assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN);

        assertSavedUser();

        verify(userRepository).existsByEmail(registerUserDto.email());
        verify(roleService).getByName(ROLE_USER);
        verify(passwordEncoder).encode(PASSWORD);
        verify(loginService).execute(loginRequestDto, context);

    }

    private RegisterUserDto registerRequest() {
        return new RegisterUserDto(EMAIL, PASSWORD);
    }

    private User savedUser() {
        return User.builder()
                .id(1L)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .roles(Set.of(userRole()))
                .build();
    }

    private Role userRole() {
        return Role.builder()
                .name(ROLE_USER)
                .build();
    }

    private AuthTokenResponseDto authToken() {
        return new AuthTokenResponseDto(ACCESS_TOKEN, REFRESH_TOKEN);
    }

    private LoginRequestDto loginRequest() {
        return new LoginRequestDto(EMAIL, PASSWORD);
    }

    private void assertSavedUser() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();
        assertThat(userToSave.getEmail()).isEqualTo(EMAIL);
        assertThat(userToSave.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(userToSave.getRoles())
                .extracting(Role::getName)
                .containsExactly(ROLE_USER);
    }

    private RefreshTokenContextDto refreshTokenContext() {
        return new RefreshTokenContextDto(IP, USER_AGENT, DEVICE);
    }

}
