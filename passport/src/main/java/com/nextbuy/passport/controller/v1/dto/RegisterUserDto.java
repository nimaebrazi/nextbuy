package com.nextbuy.passport.controller.v1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record RegisterUserDto(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
