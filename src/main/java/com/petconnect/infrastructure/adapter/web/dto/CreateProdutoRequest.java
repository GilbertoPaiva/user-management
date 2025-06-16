package com.petconnect.infrastructure.adapter.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProdutoRequest {
    
    @NotNull(message = "ID do lojista é obrigatório")
    private UUID lojistaId;
    
    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;
    
    private String description;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal price;
    
    private String photoUrl;
    
    @NotBlank(message = "Unidade de medida é obrigatória")
    private String unitOfMeasure;
}
