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
public class CreateServicoRequest {
    
    @NotNull(message = "ID do veterinário é obrigatório")
    private UUID veterinarioId;
    
    @NotBlank(message = "Nome do serviço é obrigatório")
    private String nome;
    
    private String description;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    private BigDecimal price;
}
