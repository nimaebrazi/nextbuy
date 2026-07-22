package com.nextbuy.passport.unit.service;

import com.nextbuy.passport.repository.RoleRepository;
import com.nextbuy.passport.service.RoleService;
import com.nextbuy.passport.support.fixtures.Roles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@Tags({@Tag("unit"), @Tag("service")})
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleServiceTests")
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void getByName_WhenNameIsNullOrBlank_ShouldReturnNull(){
        var nullResult = roleService.getByName(null);
        var blankResult1 = roleService.getByName("");
        var blankResult2 = roleService.getByName("   ");

        assertThat(nullResult).isNull();
        assertThat(blankResult1).isNull();
        assertThat(blankResult2).isNull();
    }

    @Test
    void getByName_WhenRoleNameDoesNotExist_ShouldReturnNull(){
        String roleName = "ROLE_TEST";

        given(roleRepository.findByName(roleName)).willReturn(Optional.empty());

        var roleResult = roleService.getByName(roleName);

        assertThat(roleResult).isNull();

        verify(roleRepository).findByName(roleName);
    }

    @Test
    void getByName_WhenRoleNameExists_ShouldReturnRole(){
        String roleName = "ROLE_TEST";
        var expectedRole = Roles.withIdAndName(roleName);

        given(roleRepository.findByName(roleName)).willReturn(Optional.of(expectedRole));

        var role = roleService.getByName(roleName);

        assertThat(role).isNotNull();
        assertThat(role.getId()).isNotNull();
        assertThat(role.getName()).isEqualTo(roleName);
        assertThat(role.getDescription()).isNotBlank().isEqualTo(expectedRole.getDescription());

        verify(roleRepository).findByName(roleName);
    }
}
