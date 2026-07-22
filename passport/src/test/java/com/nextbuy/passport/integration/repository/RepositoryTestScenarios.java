package com.nextbuy.passport.integration.repository;


import com.nextbuy.passport.domain.RefreshToken;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.support.fixtures.Roles;
import com.nextbuy.passport.support.fixtures.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class RepositoryTestScenarios {

    private final RepositoryTestData data;

    public record UserWithToken(User user, RefreshToken token) {}

    public UserWithToken userWithActiveRefreshToken(String email, String tokenHash) {
        return userWithActiveRefreshToken(
                Users.withEmailAndPassword(email, Users.DEFAULT_PASSWORD),
                tokenHash
        );
    }

    public UserWithToken userWithActiveRefreshToken(User userFixture, String tokenHash) {
        User savedUser = data.persistUser(userFixture);
        RefreshToken token = data.persistRefreshToken(savedUser, tokenHash);
        return new UserWithToken(savedUser, token);
    }

    public User userWithRandomRoles(String email, int roleCount) {
        return data.persistUser(email, Users.DEFAULT_PASSWORD, Roles.random(roleCount));
    }

}
