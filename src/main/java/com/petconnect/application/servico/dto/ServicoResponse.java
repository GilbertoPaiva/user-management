package com.petconnect.application.servico.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ServicoResponse {
    private UUID id;
    private String nome;
    private String description;
    private BigDecimal price;
    private UUID veterinarioId;
} 