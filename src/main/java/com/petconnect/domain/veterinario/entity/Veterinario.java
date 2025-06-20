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
    private String localizacao;
    private String numeroContato;
    private String horariosFuncionamento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateInfo(String nome, String location, String contactNumber, String businessHours) {
        this.nome = nome;
        this.localizacao = location;
        this.numeroContato = contactNumber;
        this.horariosFuncionamento = businessHours;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidCrmv() {
        return crmv != null && !crmv.trim().isEmpty();
    }

    public String getLocation() {
        return localizacao;
    }

    public void setLocation(String location) {
        this.localizacao = location;
    }

    public String getContactNumber() {
        return numeroContato;
    }

    public void setContactNumber(String contactNumber) {
        this.numeroContato = contactNumber;
    }

    public String getBusinessHours() {
        return horariosFuncionamento;
    }

    public void setBusinessHours(String businessHours) {
        this.horariosFuncionamento = businessHours;
    }

    public String getEmail() {
        return crmv + "@veterinario"; // Ajuste conforme sua lógica real, ou adicione um campo email se necessário
    }

    public static class VeterinarioBuilder {
        public VeterinarioBuilder location(String location) {
            this.localizacao = location;
            return this;
        }
        public VeterinarioBuilder contactNumber(String contactNumber) {
            this.numeroContato = contactNumber;
            return this;
        }
        public VeterinarioBuilder businessHours(String businessHours) {
            this.horariosFuncionamento = businessHours;
            return this;
        }
    }
}
