package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.domain.produto.port.ProdutoRepositoryPort;
import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.domain.servico.port.ServicoRepositoryPort;
import com.petconnect.infrastructure.adapter.web.dto.ProdutoResponse;
import com.petconnect.infrastructure.adapter.web.dto.ServicoResponse;
import com.petconnect.infrastructure.adapter.web.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tutor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TutorController {

    private final ProdutoRepositoryPort produtoRepository;
    private final ServicoRepositoryPort servicoRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        

        Page<Produto> produtosPage = produtoRepository.findAll(pageable);
        Page<ProdutoResponse> produtoResponses = produtosPage.map(this::mapToProdutoResponse);
        

        Page<Servico> servicosPage = servicoRepository.findAll(pageable);
        Page<ServicoResponse> servicoResponses = servicosPage.map(this::mapToServicoResponse);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("produtos", produtoResponses);
        dashboard.put("servicos", servicoResponses);
        dashboard.put("totalProdutos", produtosPage.getTotalElements());
        dashboard.put("totalServicos", servicosPage.getTotalElements());
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard do tutor", dashboard));
    }

    @GetMapping("/produtos")
    public ResponseEntity<ApiResponse<Page<ProdutoResponse>>> getProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> produtosPage;
        
        if (search != null && !search.trim().isEmpty()) {
            produtosPage = produtoRepository.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search, search, pageable);
        } else {
            produtosPage = produtoRepository.findAll(pageable);
        }
        
        Page<ProdutoResponse> responsePage = produtosPage.map(this::mapToProdutoResponse);
        
        return ResponseEntity.ok(ApiResponse.success("Produtos disponíveis", responsePage));
    }

    @GetMapping("/servicos")
    public ResponseEntity<ApiResponse<Page<ServicoResponse>>> getServicos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Servico> servicosPage;
        
        if (search != null && !search.trim().isEmpty()) {
            servicosPage = servicoRepository.findByNomeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search, search, pageable);
        } else {
            servicosPage = servicoRepository.findAll(pageable);
        }
        
        Page<ServicoResponse> responsePage = servicosPage.map(this::mapToServicoResponse);
        
        return ResponseEntity.ok(ApiResponse.success("Serviços disponíveis", responsePage));
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

    private ServicoResponse mapToServicoResponse(Servico servico) {
        return ServicoResponse.builder()
                .id(servico.getId())
                .veterinarioId(servico.getVeterinarioId())
                .nome(servico.getNome())
                .description(servico.getDescription())
                .price(servico.getPrice())
                .createdAt(servico.getCreatedAt())
                .updatedAt(servico.getUpdatedAt())
                .build();
    }
}
