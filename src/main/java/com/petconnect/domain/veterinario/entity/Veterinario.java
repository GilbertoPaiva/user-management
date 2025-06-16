package com.petconnect.domain.veterinario.entity;

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
public class Veterinario {
    private UUID id;
    private UUID userId;
    private String nome;
    private String crmv;
    private String location;
    private String contactNumber;
    private String businessHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateInfo(String nome, String location, String contactNumber, String businessHours) {
        this.nome = nome;
        this.location = location;
        this.contactNumber = contactNumber;
        this.businessHours = businessHours;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidCrmv() {
        return crmv != null && !crmv.trim().isEmpty();
    }
}
