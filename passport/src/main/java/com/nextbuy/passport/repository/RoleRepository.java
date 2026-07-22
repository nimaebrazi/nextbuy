package com.nextbuy.passport.repository;

import com.nextbuy.passport.domain.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
}
