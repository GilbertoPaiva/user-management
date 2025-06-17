package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.domain.produto.port.ProdutoRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.entity.ProdutoJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.ProdutoMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.ProdutoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProdutoRepositoryAdapter implements ProdutoRepositoryPort {

    private final ProdutoJpaRepository produtoJpaRepository;
    private final ProdutoMapper produtoMapper;

    @Override
    public Produto save(Produto produto) {
        ProdutoJpaEntity jpaEntity = produtoMapper.toJpaEntity(produto);
        ProdutoJpaEntity savedEntity = produtoJpaRepository.save(jpaEntity);
        return produtoMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Produto> findById(UUID id) {
        return produtoJpaRepository.findById(id)
                .map(produtoMapper::toDomainEntity);
    }

    @Override
    public List<Produto> findByLojistaId(UUID lojistaId) {
        List<UUID> produtoIds = produtoJpaRepository.findIdsByLojistaId(lojistaId);
        return produtoIds.stream()
                .map(id -> produtoJpaRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(produtoMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Produto> findAll(Pageable pageable) {
        return produtoJpaRepository.findAll(pageable)
                .map(produtoMapper::toDomainEntity);
    }

    @Override
    public Page<Produto> findByLojistaId(UUID lojistaId, Pageable pageable) {
        return produtoJpaRepository.findByLojistaId(lojistaId, pageable)
                .map(produtoMapper::toDomainEntity);
    }

    @Override
    public void deleteById(UUID id) {
        produtoJpaRepository.deleteById(id);
    }

    @Override
    public Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable) {
        return produtoJpaRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(produtoMapper::toDomainEntity);
    }

    @Override
    public Page<Produto> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return produtoJpaRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(produtoMapper::toDomainEntity);
    }

    @Override
    public Page<Produto> findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nome, String description, Pageable pageable) {
        return produtoJpaRepository.findByNomeOrDescriptionContaining(nome, pageable)
                .map(produtoMapper::toDomainEntity);
    }
}
