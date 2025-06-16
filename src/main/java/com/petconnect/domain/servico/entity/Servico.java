package com.petconnect.domain.servico.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servico {
    private UUID id;
    private UUID veterinarioId;
    private String nome;
    private String description;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateInfo(String nome, String description, BigDecimal price) {
        this.nome = nome;
        this.description = description;
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
