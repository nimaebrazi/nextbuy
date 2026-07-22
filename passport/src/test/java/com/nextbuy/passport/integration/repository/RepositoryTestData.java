package com.nextbuy.passport.integration.repository;


import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.support.fixtures.RefreshTokens;
import com.nextbuy.passport.support.fixtures.Roles;
import com.nextbuy.passport.support.fixtures.Users;
import com.nextbuy.passport.repository.jpa.JpaRefreshTokenRepository;
import com.nextbuy.passport.repository.jpa.JpaRoleRepository;
import com.nextbuy.passport.repository.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.HashSet;
import java.util.List;

@TestComponent
@RequiredArgsConstructor
public class RepositoryTestData {

    private final JpaRoleRepository roleRepository;
    private final JpaUserRepository userRepository;
    private final JpaRefreshTokenRepository refreshTokenRepository;

    public Role persistRole(String name, String description) {
        return roleRepository.saveAndFlush(Roles.named(name, description));
    }

    public List<Role> persistRoles(List<Role> roles) {
        return roleRepository.saveAllAndFlush(roles);
    }

    public Role existingRole(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + name));
    }

    public User persistUser(String email, String password, List<Role> roles) {
        List<Role> savedRoles = persistRoles(roles);
        User user = Users.withRoles(email, password, savedRoles);
        return userRepository.saveAndFlush(user);
    }

    public User persistUser(String email, String password) {
        return persistUser(email, password, List.of());
    }

    public User persistUser(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return userRepository.saveAndFlush(user);
        }
        List<Role> savedRoles = persistRoles(user.getRoles().stream().toList());
        user.setRoles(new HashSet<>(savedRoles));
        return userRepository.saveAndFlush(user);
    }

    public RefreshToken persistRefreshToken(RefreshToken token) {
        return refreshTokenRepository.saveAndFlush(token);
    }

    public RefreshToken persistRefreshToken(User persistedUser, String tokenHash) {
        return persistRefreshToken(RefreshTokens.forUser(persistedUser, tokenHash));
    }

}
