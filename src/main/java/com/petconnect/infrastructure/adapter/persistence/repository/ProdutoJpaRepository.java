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
    
    @Query("SELECT p.id FROM ProdutoJpaEntity p WHERE p.lojistaId = :lojistaId")
    List<UUID> findIdsByLojistaId(@Param("lojistaId") UUID lojistaId);
    
    @Query("SELECT p FROM ProdutoJpaEntity p WHERE p.lojistaId = :lojistaId")
    Page<ProdutoJpaEntity> findByLojistaId(@Param("lojistaId") UUID lojistaId, Pageable pageable);
    
    @Query("SELECT p FROM ProdutoJpaEntity p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<ProdutoJpaEntity> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);
    
    @Query("SELECT p FROM ProdutoJpaEntity p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<ProdutoJpaEntity> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, 
                                            @Param("maxPrice") BigDecimal maxPrice, 
                                            Pageable pageable);
    
    @Query("SELECT p FROM ProdutoJpaEntity p WHERE " +
           "LOWER(p.nome) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ProdutoJpaEntity> findByNomeOrDescriptionContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(p.id) FROM ProdutoJpaEntity p WHERE p.lojistaId = :lojistaId")
    long countByLojistaId(@Param("lojistaId") UUID lojistaId);
}
