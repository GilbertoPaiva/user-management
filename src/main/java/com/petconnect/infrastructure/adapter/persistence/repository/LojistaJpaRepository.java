package com.petconnect.infrastructure.adapter.persistence.repository;

import com.petconnect.infrastructure.adapter.persistence.entity.LojistaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LojistaJpaRepository extends JpaRepository<LojistaJpaEntity, UUID> {
    
    Optional<LojistaJpaEntity> findByUserId(UUID userId);
    Optional<LojistaJpaEntity> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
}
