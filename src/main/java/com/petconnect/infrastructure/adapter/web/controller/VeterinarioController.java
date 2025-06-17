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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/veterinario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VeterinarioController {

    private final CreateServicoUseCase createServicoUseCase;
    private final ServicoRepositoryPort servicoRepository;

    @GetMapping("/dashboard/{veterinarioId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(@PathVariable UUID veterinarioId) {
        List<Servico> servicos = servicoRepository.findByVeterinarioId(veterinarioId);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalServicos", servicos.size());
        dashboard.put("servicos", servicos.stream()
                .map(this::mapToServicoResponse)
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard do veterinário", dashboard));
    }

    @PostMapping("/{veterinarioId}/servicos")
    public ResponseEntity<ApiResponse<ServicoResponse>> createServico(
            @PathVariable UUID veterinarioId,
            @Valid @RequestBody CreateServicoRequest request) {
        
        CreateServicoCommand command = CreateServicoCommand.builder()
                .veterinarioId(veterinarioId)
                .nome(request.getNome())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();

        Servico servico = createServicoUseCase.execute(command);
        ServicoResponse response = mapToServicoResponse(servico);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Serviço criado com sucesso", response));
    }

    @GetMapping("/{veterinarioId}/servicos")
    public ResponseEntity<ApiResponse<List<ServicoResponse>>> getServicos(@PathVariable UUID veterinarioId) {
        List<Servico> servicos = servicoRepository.findByVeterinarioId(veterinarioId);
        List<ServicoResponse> responses = servicos.stream()
                .map(this::mapToServicoResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Serviços do veterinário", responses));
    }

    @GetMapping("/{veterinarioId}/servicos/{servicoId}")
    public ResponseEntity<ApiResponse<ServicoResponse>> getServicoById(
            @PathVariable UUID veterinarioId,
            @PathVariable UUID servicoId) {
        
        return servicoRepository.findById(servicoId)
                .filter(servico -> servico.getVeterinarioId().equals(veterinarioId))
                .map(servico -> {
                    ServicoResponse response = mapToServicoResponse(servico);
                    return ResponseEntity.ok(ApiResponse.success("Detalhes do serviço", response));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{veterinarioId}/servicos/{servicoId}")
    public ResponseEntity<ApiResponse<ServicoResponse>> updateServico(
            @PathVariable UUID veterinarioId,
            @PathVariable UUID servicoId,
            @Valid @RequestBody CreateServicoRequest request) {
        
        return servicoRepository.findById(servicoId)
                .filter(servico -> servico.getVeterinarioId().equals(veterinarioId))
                .map(servico -> {
                    servico.updateInfo(request.getNome(), request.getDescription(), request.getPrice());
                    Servico updatedServico = servicoRepository.save(servico);
                    ServicoResponse response = mapToServicoResponse(updatedServico);
                    return ResponseEntity.ok(ApiResponse.success("Serviço atualizado com sucesso", response));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{veterinarioId}/servicos/{servicoId}")
    public ResponseEntity<ApiResponse<String>> deleteServico(
            @PathVariable UUID veterinarioId,
            @PathVariable UUID servicoId) {
        
        return servicoRepository.findById(servicoId)
                .filter(servico -> servico.getVeterinarioId().equals(veterinarioId))
                .map(servico -> {
                    servicoRepository.deleteById(servicoId);
                    return ResponseEntity.ok(ApiResponse.success("Serviço removido com sucesso"));
                })
                .orElse(ResponseEntity.notFound().build());
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
