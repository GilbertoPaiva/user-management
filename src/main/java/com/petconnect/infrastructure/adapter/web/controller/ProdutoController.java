package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.produto.dto.CreateProdutoRequest;
import com.petconnect.application.produto.dto.ProdutoResponse;
import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.domain.produto.port.ProdutoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    private final ProdutoRepositoryPort produtoRepository;

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@RequestBody CreateProdutoRequest request) {
        Produto produto = new Produto();
        produto.setNome(request.getNome());
        produto.setDescription(request.getDescription());
        produto.setPrice(request.getPrice());
        produto.setUnitOfMeasure(request.getUnitOfMeasure());
        produto.setPhotoUrl(request.getPhotoUrl());
        produto.setLojistaId(request.getLojistaId());
        Produto salvo = produtoRepository.save(produto);
        return ResponseEntity.ok(toResponse(salvo));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() {
        List<Produto> produtos = produtoRepository.findAll();
        List<ProdutoResponse> responses = produtos.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable UUID id, @RequestBody CreateProdutoRequest request) {
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto == null) return ResponseEntity.notFound().build();
        produto.setNome(request.getNome());
        produto.setDescription(request.getDescription());
        produto.setPrice(request.getPrice());
        produto.setUnitOfMeasure(request.getUnitOfMeasure());
        produto.setPhotoUrl(request.getPhotoUrl());
        Produto atualizado = produtoRepository.save(produto);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ProdutoResponse toResponse(Produto produto) {
        ProdutoResponse resp = new ProdutoResponse();
        resp.setId(produto.getId());
        resp.setNome(produto.getNome());
        resp.setDescription(produto.getDescription());
        resp.setPrice(produto.getPrice());
        resp.setUnitOfMeasure(produto.getUnitOfMeasure());
        resp.setPhotoUrl(produto.getPhotoUrl());
        resp.setLojistaId(produto.getLojistaId());
        return resp;
    }
}
