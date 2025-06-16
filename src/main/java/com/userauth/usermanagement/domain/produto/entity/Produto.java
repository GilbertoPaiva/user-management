package com.userauth.usermanagement.domain.produto.entity;

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
public class Produto {
    private UUID id;
    private UUID lojistaId;
    private String nome;
    private String description;
    private BigDecimal price;
    private String photoUrl;
    private String unitOfMeasure;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateInfo(String nome, String description, BigDecimal price, String unitOfMeasure) {
        this.nome = nome;
        this.description = description;
        this.price = price;
        this.unitOfMeasure = unitOfMeasure;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
