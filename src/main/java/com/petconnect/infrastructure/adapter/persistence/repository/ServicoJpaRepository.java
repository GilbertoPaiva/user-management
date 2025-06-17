package com.petconnect.infrastructure.adapter.persistence.repository;

import com.petconnect.infrastructure.adapter.persistence.entity.ServicoJpaEntity;
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
public interface ServicoJpaRepository extends JpaRepository<ServicoJpaEntity, UUID> {
    
    List<ServicoJpaEntity> findByVeterinarioId(UUID veterinarioId);
    Page<ServicoJpaEntity> findByVeterinarioId(UUID veterinarioId, Pageable pageable);
    
    // Métodos de busca avançada
    Page<ServicoJpaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<ServicoJpaEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    @Query("SELECT s FROM ServicoJpaEntity s WHERE " +
           "LOWER(s.nome) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ServicoJpaEntity> findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        @Param("search") String search, Pageable pageable);
}
