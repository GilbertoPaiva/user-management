package com.petconnect.infrastructure.adapter.persistence.repository;

import com.petconnect.infrastructure.adapter.persistence.entity.ProdutoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, UUID> {
    
    List<ProdutoJpaEntity> findByLojistaId(UUID lojistaId);
    Page<ProdutoJpaEntity> findByLojistaId(UUID lojistaId, Pageable pageable);
    
    // Métodos de busca avançada
    Page<ProdutoJpaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<ProdutoJpaEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    @Query("SELECT p FROM ProdutoJpaEntity p WHERE " +
           "LOWER(p.nome) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ProdutoJpaEntity> findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        @Param("search") String search, Pageable pageable);
}
