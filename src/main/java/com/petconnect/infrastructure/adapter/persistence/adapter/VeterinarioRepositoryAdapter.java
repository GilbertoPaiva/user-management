package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.veterinario.entity.Veterinario;
import com.petconnect.domain.veterinario.port.VeterinarioRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.entity.VeterinarioJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.VeterinarioMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.VeterinarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VeterinarioRepositoryAdapter implements VeterinarioRepositoryPort {
    private final VeterinarioJpaRepository jpaRepository;
    private final VeterinarioMapper mapper;

    @Override
    public Veterinario save(Veterinario veterinario) {
        VeterinarioJpaEntity entity = mapper.toEntity(veterinario);
        VeterinarioJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Veterinario> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Veterinario> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<Veterinario> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Veterinario> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }
} 