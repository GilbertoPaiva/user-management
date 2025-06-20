package com.petconnect.infrastructure.adapter.web.controller;

import com.petconnect.application.admin.dto.AdminDashboardResponse;
import com.petconnect.domain.lojista.entity.Lojista;
import com.petconnect.domain.lojista.port.LojistaRepositoryPort;
import com.petconnect.domain.tutor.entity.Tutor;
import com.petconnect.domain.tutor.port.TutorRepositoryPort;
import com.petconnect.domain.veterinario.entity.Veterinario;
import com.petconnect.domain.veterinario.port.VeterinarioRepositoryPort;
import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.port.UserRepositoryPort;
import com.petconnect.domain.produto.entity.Produto;
import com.petconnect.domain.produto.port.ProdutoRepositoryPort;
import com.petconnect.domain.servico.entity.Servico;
import com.petconnect.domain.servico.port.ServicoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final UserRepositoryPort userRepository;
    private final LojistaRepositoryPort lojistaRepository;
    private final TutorRepositoryPort tutorRepository;
    private final VeterinarioRepositoryPort veterinarioRepository;
    private final ProdutoRepositoryPort produtoRepository;
    private final ServicoRepositoryPort servicoRepository;

    @GetMapping("/totais")
    public ResponseEntity<AdminDashboardResponse> getTotais() {
        AdminDashboardResponse resp = new AdminDashboardResponse();
        resp.setTotalAdmins(userRepository.findAll().stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getUserType().name())).count());
        resp.setTotalTutores(tutorRepository.findAll().size());
        resp.setTotalVeterinarios(veterinarioRepository.findAll().size());
        resp.setTotalLojistas(lojistaRepository.findAll().size());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<AdminDashboardResponse.UserSummary>> listarUsuarios(@RequestParam(required = false) String nome, @RequestParam(required = false) String tipo) {
        List<User> users = userRepository.findAll();
        List<AdminDashboardResponse.UserSummary> result = users.stream()
            .filter(u -> (nome == null || u.getFullName().toLowerCase().contains(nome.toLowerCase())))
            .filter(u -> (tipo == null || u.getUserType().name().equalsIgnoreCase(tipo)))
            .map(u -> {
                AdminDashboardResponse.UserSummary s = new AdminDashboardResponse.UserSummary();
                s.setId(u.getId());
                s.setNome(u.getFullName());
                s.setEmail(u.getEmail());
                s.setTipo(u.getUserType().name());
                return s;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<AdminDashboardResponse.UserDetail> detalharUsuario(@PathVariable UUID id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
        User user = userOpt.get();
        AdminDashboardResponse.UserDetail detail = new AdminDashboardResponse.UserDetail();
        detail.setId(user.getId());
        detail.setNome(user.getFullName());
        detail.setEmail(user.getEmail());
        detail.setTipo(user.getUserType().name());
        // Dados cadastrais e associações por tipo
        switch (user.getUserType().name()) {
            case "LOJISTA":
                Lojista lojista = lojistaRepository.findAll().stream().filter(l -> l.getEmail().equalsIgnoreCase(user.getEmail())).findFirst().orElse(null);
                detail.setDadosCadastrais(lojista);
                List<Produto> produtos = produtoRepository.findAll().stream().filter(p -> p.getLojistaId().equals(lojista != null ? lojista.getId() : null)).collect(Collectors.toList());
                detail.setProdutos(new ArrayList<>(produtos));
                break;
            case "TUTOR":
                Tutor tutor = tutorRepository.findAll().stream().filter(t -> t.getEmail().equalsIgnoreCase(user.getEmail())).findFirst().orElse(null);
                detail.setDadosCadastrais(tutor);
                // Animais: não implementado, lista vazia
                detail.setAnimais(new ArrayList<>());
                break;
            case "VETERINARIO":
                Veterinario vet = veterinarioRepository.findAll().stream().filter(v -> v.getEmail().equalsIgnoreCase(user.getEmail())).findFirst().orElse(null);
                detail.setDadosCadastrais(vet);
                List<Servico> servicos = servicoRepository.findAll().stream().filter(s -> s.getVeterinarioId().equals(vet != null ? vet.getId() : null)).collect(Collectors.toList());
                detail.setServicos(new ArrayList<>(servicos));
                break;
            case "ADMIN":
                detail.setDadosCadastrais(user);
                break;
        }
        return ResponseEntity.ok(detail);
    }
} 