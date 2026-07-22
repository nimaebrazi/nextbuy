package com.nextbuy.passport.dto;

import com.nextbuy.passport.domain.User;

public record UserProfileDto(
        Long id,
        String email
) {
    public static UserProfileDto from(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getEmail()
        );
    }
}
