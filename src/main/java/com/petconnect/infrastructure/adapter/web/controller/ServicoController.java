package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.servico.usecase.CreateServicoCommand;
import com.petconnect.application.servico.usecase.CreateServicoUseCase;
import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.domain.servico.port.ServicoRepositoryPort;
import com.petconnect.infrastructure.adapter.web.dto.CreateServicoRequest;
import com.petconnect.infrastructure.adapter.web.dto.ServicoResponse;
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
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicoController {

    private final CreateServicoUseCase createServicoUseCase;
    private final ServicoRepositoryPort servicoRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ServicoResponse>> createServico(@Valid @RequestBody CreateServicoRequest request) {
        CreateServicoCommand command = CreateServicoCommand.builder()
                .veterinarioId(request.getVeterinarioId())
                .nome(request.getNome())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();

        Servico servico = createServicoUseCase.execute(command);
        ServicoResponse response = mapToServicoResponse(servico);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Serviço criado com sucesso", response));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<ApiResponse<List<ServicoResponse>>> getServicosByVeterinario(@PathVariable UUID veterinarioId) {
        List<Servico> servicos = servicoRepository.findByVeterinarioId(veterinarioId);
        List<ServicoResponse> responses = servicos.stream()
                .map(this::mapToServicoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Serviços do veterinário", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicoResponse>> getServicoById(@PathVariable UUID id) {
        return servicoRepository.findById(id)
                .map(servico -> {
                    ServicoResponse response = mapToServicoResponse(servico);
                    return ResponseEntity.ok(ApiResponse.success("Detalhes do serviço", response));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicoResponse>> updateServico(
            @PathVariable UUID id,
            @Valid @RequestBody CreateServicoRequest request) {
        
        return servicoRepository.findById(id)
                .map(servico -> {
                    servico.updateInfo(request.getNome(), request.getDescription(), request.getPrice());
                    Servico updatedServico = servicoRepository.save(servico);
                    ServicoResponse response = mapToServicoResponse(updatedServico);
                    return ResponseEntity.ok(ApiResponse.success("Serviço atualizado com sucesso", response));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteServico(@PathVariable UUID id) {
        return servicoRepository.findById(id)
                .map(servico -> {
                    servicoRepository.deleteById(id);
                    return ResponseEntity.ok(ApiResponse.success("Serviço removido com sucesso"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServicoResponse>>> getAllServicos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Servico> servicosPage = servicoRepository.findAll(pageable);
        
        Page<ServicoResponse> responsePage = servicosPage.map(this::mapToServicoResponse);
        
        return ResponseEntity.ok(ApiResponse.success("Lista de serviços", responsePage));
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
