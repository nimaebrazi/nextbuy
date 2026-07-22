package com.nextbuy.passport.dto;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record GenerateJwtTokenDto(
        long userId,
        String email,
        Collection<? extends GrantedAuthority> auths
) {
}
