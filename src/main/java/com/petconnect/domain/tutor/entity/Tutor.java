package com.petconnect.domain.tutor.entity;

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
public class Tutor {
    private UUID id;
    private UUID userId;
    private String nome;
    private String cnpj;
    private String location;
    private String contactNumber;
    private String guardian;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateInfo(String nome, String location, String contactNumber, String guardian) {
        this.nome = nome;
        this.location = location;
        this.contactNumber = contactNumber;
        this.guardian = guardian;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasCnpj() {
        return cnpj != null && !cnpj.trim().isEmpty();
    }
}
