package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.lojista.dto.CreateLojistaRequest;
import com.petconnect.domain.lojista.entity.Lojista;
import com.petconnect.domain.lojista.port.LojistaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lojistas")
@RequiredArgsConstructor
public class LojistaController {
    private final LojistaRepositoryPort lojistaRepository;

    @PostMapping
    public ResponseEntity<Lojista> cadastrar(@RequestBody CreateLojistaRequest request) {
        Lojista lojista = new Lojista();
        lojista.setNome(request.getNome());
        lojista.setCnpj(request.getCnpj());
        lojista.setEmail(request.getEmail());
        lojista.setSenha(request.getSenha());
        lojista.setLocalizacao(request.getLocalizacao());
        lojista.setNumeroContato(request.getNumeroContato());
        lojista.setTipoLoja(request.getTipoLoja() != null ? com.petconnect.domain.lojista.entity.StoreType.valueOf(request.getTipoLoja().toUpperCase()) : null);
        lojista.setPerguntaSeguranca1(request.getPerguntaSeguranca1());
        lojista.setRespostaSeguranca1(request.getRespostaSeguranca1());
        lojista.setPerguntaSeguranca2(request.getPerguntaSeguranca2());
        lojista.setRespostaSeguranca2(request.getRespostaSeguranca2());
        lojista.setPerguntaSeguranca3(request.getPerguntaSeguranca3());
        lojista.setRespostaSeguranca3(request.getRespostaSeguranca3());
        Lojista salvo = lojistaRepository.save(lojista);
        return ResponseEntity.ok(salvo);
    }
} 