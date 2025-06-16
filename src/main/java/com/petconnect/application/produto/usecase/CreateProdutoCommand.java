package com.petconnect.application.produto.usecase;

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
public class CreateProdutoCommand {
    private UUID lojistaId;
    private String nome;
    private String description;
    private BigDecimal price;
    private String photoUrl;
    private String unitOfMeasure;
}
