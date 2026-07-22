package com.nextbuy.passport.integration.repository;

import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.support.fixtures.Roles;
import com.nextbuy.passport.repository.RoleRepository;
import com.nextbuy.passport.repository.jpa.JpaRoleRepository;
import com.nextbuy.passport.repository.jpa.JpaRoleRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tags({@Tag("integration"), @Tag("repository")})
@Import({
        JpaRoleRepositoryAdapter.class,
        RepositoryTestData.class
})
class JpaRoleRepositoryIT extends JpaRepositoryIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JpaRoleRepository jpaRoleRepository;

    @BeforeEach
    void cleanup() {
        jpaRoleRepository.deleteAll();
    }

    @Test
    void findByName_returnsPersistedRole() {
        jpaRoleRepository.saveAll(List.of(
                Roles.named("ROLE_TEST_1", "ROLE_TEST_1_DESCRIPTION"),
                Roles.named("ROLE_TEST_2", "ROLE_TEST_2_DESCRIPTION"),
                Roles.named("ROLE_TEST_3", "ROLE_TEST_3_DESCRIPTION")
        ));

        assertThat(roleRepository.findByName("ROLE_TEST_1"))
                .isPresent()
                .get()
                .satisfies(role -> {
                    assertThat(role.getId()).isNotNull();
                    assertThat(role.getName()).isEqualTo("ROLE_TEST_1");
                    assertThat(role.getDescription()).isEqualTo("ROLE_TEST_1_DESCRIPTION");
                });

        assertThat(roleRepository.findByName("ROLE_TEST_2"))
                .isPresent()
                .get()
                .satisfies(role -> {
                    assertThat(role.getId()).isNotNull();
                    assertThat(role.getName()).isEqualTo("ROLE_TEST_2");
                    assertThat(role.getDescription()).isEqualTo("ROLE_TEST_2_DESCRIPTION");
                });

        assertThat(roleRepository.findByName("ROLE_TEST_3"))
                .isPresent()
                .get()
                .satisfies(role -> {
                    assertThat(role.getId()).isNotNull();
                    assertThat(role.getName()).isEqualTo("ROLE_TEST_3");
                    assertThat(role.getDescription()).isEqualTo("ROLE_TEST_3_DESCRIPTION");
                });
    }

    @Test
    void findByName_returnsEmptyWhenRoleDoesNotExist() {
        assertThat(roleRepository.findByName("ROLE_MISSING")).isEmpty();
    }

    @Test
    void save_rejectsDuplicateRoleName() {
        List<Role> roles = List.of(
                Roles.named("ROLE_TEST_1", "ROLE_TEST_1_DESCRIPTION"),
                Roles.named("ROLE_TEST_1", "ROLE_TEST_1_DESCRIPTION")
        );

        assertThatThrownBy(() -> jpaRoleRepository.saveAll(roles))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
