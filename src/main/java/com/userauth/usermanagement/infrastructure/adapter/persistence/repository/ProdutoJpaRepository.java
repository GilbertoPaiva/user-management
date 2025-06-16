package com.userauth.usermanagement.infrastructure.adapter.persistence.repository;

import com.userauth.usermanagement.infrastructure.adapter.persistence.entity.ProdutoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, UUID> {
    
    List<ProdutoJpaEntity> findByLojistaId(UUID lojistaId);
    Page<ProdutoJpaEntity> findByLojistaId(UUID lojistaId, Pageable pageable);
}
