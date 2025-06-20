package com.petconnect.application.produto.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateProdutoRequest {
    private String nome;
    private String description;
    private BigDecimal price;
    private String unitOfMeasure;
    @NotBlank(message = "A imagem do produto é obrigatória")
    private String photoUrl; // opcional
    private UUID lojistaId;
} 