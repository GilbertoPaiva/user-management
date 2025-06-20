package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.servico.dto.CreateServicoRequest;
import com.petconnect.application.servico.dto.ServicoResponse;
import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.domain.servico.port.ServicoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoController {
    private final ServicoRepositoryPort servicoRepository;

    @PostMapping
    public ResponseEntity<ServicoResponse> criar(@RequestBody CreateServicoRequest request) {
        Servico servico = new Servico();
        servico.setNome(request.getNome());
        servico.setDescription(request.getDescription());
        servico.setPrice(request.getPrice());
        servico.setVeterinarioId(request.getVeterinarioId());
        Servico salvo = servicoRepository.save(servico);
        return ResponseEntity.ok(toResponse(salvo));
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listar() {
        List<Servico> servicos = servicoRepository.findAll();
        List<ServicoResponse> responses = servicos.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> atualizar(@PathVariable UUID id, @RequestBody CreateServicoRequest request) {
        Servico servico = servicoRepository.findById(id).orElse(null);
        if (servico == null) return ResponseEntity.notFound().build();
        servico.setNome(request.getNome());
        servico.setDescription(request.getDescription());
        servico.setPrice(request.getPrice());
        Servico atualizado = servicoRepository.save(servico);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        servicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ServicoResponse toResponse(Servico servico) {
        ServicoResponse resp = new ServicoResponse();
        resp.setId(servico.getId());
        resp.setNome(servico.getNome());
        resp.setDescription(servico.getDescription());
        resp.setPrice(servico.getPrice());
        resp.setVeterinarioId(servico.getVeterinarioId());
        return resp;
    }
}
