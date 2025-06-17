package com.petconnect.application.servico.usecase;

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
public class CreateServicoCommand {
    private UUID veterinarioId;
    private String nome;
    private String description;
    private BigDecimal price;
}
