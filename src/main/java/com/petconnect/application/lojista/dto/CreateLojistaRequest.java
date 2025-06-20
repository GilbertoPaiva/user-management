package com.petconnect.application.lojista.dto;

import lombok.Data;

@Data
public class CreateLojistaRequest {
    private String nome;
    private String cnpj;
    private String email;
    private String senha;
    private String localizacao;
    private String numeroContato;
    private String tipoLoja; // Virtual ou Local
    private String perguntaSeguranca1;
    private String respostaSeguranca1;
    private String perguntaSeguranca2;
    private String respostaSeguranca2;
    private String perguntaSeguranca3;
    private String respostaSeguranca3;
} 