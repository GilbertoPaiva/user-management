package com.petconnect.application.veterinario.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateVeterinarioRequest {
    private String nome;
    private String crmv;
    private String localizacao;
    private String numeroContato;
    private String horariosFuncionamento; // opcional
    private UUID userId;
} 