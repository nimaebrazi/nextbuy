package com.nextbuy.passport.support.fixtures;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public final class Authorities {
    private Authorities() {
    }

    public static Set<GrantedAuthority> of(String... roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;

    }

    public static Set<GrantedAuthority> roleUser() {
        return of("ROLE_USER");
    }
}
