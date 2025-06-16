package com.userauth.usermanagement.domain.produto.port;

import com.userauth.usermanagement.domain.produto.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}