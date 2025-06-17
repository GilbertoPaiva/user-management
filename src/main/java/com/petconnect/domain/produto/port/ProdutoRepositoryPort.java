package com.petconnect.domain.produto.port;

import com.petconnect.domain.produto.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepositoryPort {
    Produto save(Produto produto);
    Optional<Produto> findById(UUID id);
    List<Produto> findByLojistaId(UUID lojistaId);
    Page<Produto> findByLojistaId(UUID lojistaId, Pageable pageable);
    void deleteById(UUID id);
    Page<Produto> findAll(Pageable pageable);
    

    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Produto> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Produto> findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nome, String description, Pageable pageable);
}