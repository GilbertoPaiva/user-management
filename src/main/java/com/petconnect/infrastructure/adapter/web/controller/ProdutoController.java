package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.produto.usecase.CreateProdutoCommand;
import com.petconnect.application.produto.usecase.CreateProdutoUseCase;
import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.domain.produto.port.ProdutoRepositoryPort;
import com.petconnect.infrastructure.adapter.web.dto.CreateProdutoRequest;
import com.petconnect.infrastructure.adapter.web.dto.ProdutoResponse;
import com.petconnect.infrastructure.adapter.web.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final CreateProdutoUseCase createProdutoUseCase;
    private final ProdutoRepositoryPort produtoRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ProdutoResponse>> createProduto(@Valid @RequestBody CreateProdutoRequest request) {
        CreateProdutoCommand command = CreateProdutoCommand.builder()
                .lojistaId(request.getLojistaId())
                .nome(request.getNome())
                .description(request.getDescription())
                .price(request.getPrice())
                .photoUrl(request.getPhotoUrl())
                .unitOfMeasure(request.getUnitOfMeasure())
                .build();

        Produto produto = createProdutoUseCase.execute(command);
        ProdutoResponse response = mapToProdutoResponse(produto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Produto criado com sucesso", response));
    }

    @GetMapping("/lojista/{lojistaId}")
    public ResponseEntity<ApiResponse<List<ProdutoResponse>>> getProdutosByLojista(@PathVariable UUID lojistaId) {
        List<Produto> produtos = produtoRepository.findByLojistaId(lojistaId);
        List<ProdutoResponse> responses = produtos.stream()
                .map(this::mapToProdutoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponse>> getProdutoById(@PathVariable UUID id) {
        return produtoRepository.findById(id)
                .map(produto -> ResponseEntity.ok(ApiResponse.success(mapToProdutoResponse(produto))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponse>> updateProduto(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProdutoRequest request) {
        
        return produtoRepository.findById(id)
                .map(produto -> {
                    produto.updateInfo(request.getNome(), request.getDescription(), 
                                     request.getPrice(), request.getUnitOfMeasure());
                    if (request.getPhotoUrl() != null) {
                        produto.setPhotoUrl(request.getPhotoUrl());
                    }
                    Produto savedProduto = produtoRepository.save(produto);
                    return ResponseEntity.ok(ApiResponse.success("Produto atualizado com sucesso", 
                                                                mapToProdutoResponse(savedProduto)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduto(@PathVariable UUID id) {
        if (produtoRepository.findById(id).isPresent()) {
            produtoRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Produto removido com sucesso", null));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProdutoResponse>>> getAllProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> produtos = produtoRepository.findAll(pageable);
        Page<ProdutoResponse> responses = produtos.map(this::mapToProdutoResponse);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    private ProdutoResponse mapToProdutoResponse(Produto produto) {
        return ProdutoResponse.builder()
                .id(produto.getId())
                .lojistaId(produto.getLojistaId())
                .nome(produto.getNome())
                .description(produto.getDescription())
                .price(produto.getPrice())
                .photoUrl(produto.getPhotoUrl())
                .unitOfMeasure(produto.getUnitOfMeasure())
                .createdAt(produto.getCreatedAt())
                .updatedAt(produto.getUpdatedAt())
                .build();
    }
}
