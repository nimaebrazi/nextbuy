package com.nextbuy.passport.service;


import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getByName(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return null;
        }

        return roleRepository.findByName(roleName).orElse(null);
    }
}
