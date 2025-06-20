package com.petconnect.domain.user.port;

import com.petconnect.domain.user.entity.Role;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(String name);
} 