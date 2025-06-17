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
    
    @Query("SELECT s.id FROM ServicoJpaEntity s WHERE s.veterinarioId = :veterinarioId")
    List<UUID> findIdsByVeterinarioId(@Param("veterinarioId") UUID veterinarioId);
    
    @Query("SELECT s FROM ServicoJpaEntity s WHERE s.veterinarioId = :veterinarioId")
    Page<ServicoJpaEntity> findByVeterinarioId(@Param("veterinarioId") UUID veterinarioId, Pageable pageable);
    
    @Query("SELECT s FROM ServicoJpaEntity s WHERE LOWER(s.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<ServicoJpaEntity> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);
    
    @Query("SELECT s FROM ServicoJpaEntity s WHERE s.price BETWEEN :minPrice AND :maxPrice")
    Page<ServicoJpaEntity> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, 
                                            @Param("maxPrice") BigDecimal maxPrice, 
                                            Pageable pageable);
    
    @Query("SELECT s FROM ServicoJpaEntity s WHERE " +
           "LOWER(s.nome) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ServicoJpaEntity> findByNomeOrDescriptionContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(s.id) FROM ServicoJpaEntity s WHERE s.veterinarioId = :veterinarioId")
    long countByVeterinarioId(@Param("veterinarioId") UUID veterinarioId);
}
