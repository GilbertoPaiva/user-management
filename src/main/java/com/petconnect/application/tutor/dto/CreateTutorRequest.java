package com.petconnect.application.tutor.dto;

import lombok.Data;

@Data
public class CreateTutorRequest {
    private String nome;
    private String email;
    private String senha;
    private String cnpj; // opcional
    private String localizacao;
    private String numeroContato;
    private String responsavel;
} 