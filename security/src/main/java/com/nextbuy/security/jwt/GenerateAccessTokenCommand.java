package com.nextbuy.security.jwt;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record GenerateAccessTokenCommand(
        long userId,
        String email,
        Collection<? extends GrantedAuthority> authorities
) {
}
