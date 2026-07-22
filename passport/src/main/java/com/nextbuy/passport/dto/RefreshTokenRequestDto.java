package com.nextbuy.passport.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank String refreshToken
) {
}
