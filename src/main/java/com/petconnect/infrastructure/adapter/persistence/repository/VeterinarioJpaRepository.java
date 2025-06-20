package com.petconnect.infrastructure.adapter.persistence.repository;

import com.petconnect.infrastructure.adapter.persistence.entity.VeterinarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VeterinarioJpaRepository extends JpaRepository<VeterinarioJpaEntity, UUID> {
    Optional<VeterinarioJpaEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
} 