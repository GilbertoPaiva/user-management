package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.user.entity.Role;
import com.petconnect.domain.user.port.RoleRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.mapper.RoleMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(roleMapper::toDomain);
    }
} 