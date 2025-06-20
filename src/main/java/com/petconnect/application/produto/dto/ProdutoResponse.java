package com.petconnect.application.produto.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProdutoResponse {
    private UUID id;
    private String nome;
    private String description;
    private BigDecimal price;
    private String unitOfMeasure;
    private String photoUrl;
    private UUID lojistaId;
} 