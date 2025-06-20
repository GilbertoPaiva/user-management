package com.petconnect.domain.lojista.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lojista {
    private UUID id;
    private UUID userId;
    private String nome;
    private String cnpj;
    private String email;
    private String senha;
    private String localizacao;
    private String numeroContato;
    private StoreType tipoLoja;
    private String perguntaSeguranca1;
    private String respostaSeguranca1;
    private String perguntaSeguranca2;
    private String respostaSeguranca2;
    private String perguntaSeguranca3;
    private String respostaSeguranca3;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateInfo(String nome, String location, String contactNumber) {
        this.nome = nome;
        this.localizacao = location;
        this.numeroContato = contactNumber;
        this.updatedAt = LocalDateTime.now();
    }
}
