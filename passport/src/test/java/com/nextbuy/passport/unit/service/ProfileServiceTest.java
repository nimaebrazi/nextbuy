package com.nextbuy.passport.unit.service;


import com.nextbuy.passport.common.advice.exception.BusinessException;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.UserProfileDto;
import com.nextbuy.passport.repository.UserRepository;
import com.nextbuy.passport.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@Tags({@Tag("unit"), @Tag("service")})
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileServiceTest")
public class ProfileServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ProfileService profileService;

    @Test
    @DisplayName("It should get user profile using email")
    void execute_WhenUserExists_ThenGetUserProfile() {
        String email = "nima@example.com";
        User expectedUser = User.builder()
                .id(1L)
                .email(email)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(expectedUser));

        UserProfileDto actualProfile = profileService.execute(email);

        assertThat(actualProfile.id()).isEqualTo(expectedUser.getId());
        assertThat(actualProfile.email()).isEqualTo(expectedUser.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("It should throw invalid access token when user does not exist")
    void execute_WhenUserDoesNotExist_ThenThrowInvalidAccessToken() {
        String email = "missing@example.com";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.execute(email))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired access token")
                .extracting("errorCode")
                .isEqualTo("INVALID_ACCESS_TOKEN");

        verify(userRepository).findByEmail(email);
    }

}
