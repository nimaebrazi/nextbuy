package com.nextbuy.passport.integration.repository;

import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.repository.UserRepository;
import com.nextbuy.passport.repository.jpa.JpaUserRepositoryAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Tags({@Tag("integration"), @Tag("repository")})
@Import({
        JpaUserRepositoryAdapter.class,
        RepositoryTestData.class,
        RepositoryTestScenarios.class
})
class JpaUserRepositoryIT extends JpaRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepositoryTestScenarios scenarios;

    @Test
    void findByEmail_returnsUserWithRoles() {
        var saved = scenarios.userWithRandomRoles("user@example.com", 2);
        List<String> roleNames = saved.getRoles().stream().map(Role::getName).toList();

        assertThat(userRepository.findByEmail(saved.getEmail()))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getId()).isEqualTo(saved.getId());
                    assertThat(found.getEmail()).isEqualTo(saved.getEmail());
                    assertThat(found.getRoles())
                            .hasSize(roleNames.size())
                            .extracting(Role::getName)
                            .containsExactlyInAnyOrderElementsOf(roleNames);
                });
    }

    @Test
    void findByEmail_returnsEmptyWhenUserDoesNotExist() {
        assertThat(userRepository.findByEmail("missing@example.com")).isEmpty();
    }

    @Test
    void existsByEmail_returnsTrueWhenUserExists() {
        var saved = scenarios.userWithRandomRoles("exists@example.com", 1);

        assertThat(userRepository.existsByEmail(saved.getEmail())).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }
}
