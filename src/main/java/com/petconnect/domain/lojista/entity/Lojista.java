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
    private String location;
    private String contactNumber;
    private StoreType storeType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateInfo(String nome, String location, String contactNumber) {
        this.nome = nome;
        this.location = location;
        this.contactNumber = contactNumber;
        this.updatedAt = LocalDateTime.now();
    }
}
