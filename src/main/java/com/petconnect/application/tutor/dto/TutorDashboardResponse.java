package com.petconnect.application.tutor.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class TutorDashboardResponse {
    private List<ProdutoCard> produtos;
    private List<ServicoCard> servicos;

    @Data
    public static class ProdutoCard {
        private UUID id;
        private String nome;
        private String description;
        private BigDecimal price;
        private String photoUrl;
        private String endereco;
        private String numeroContato;
    }

    @Data
    public static class ServicoCard {
        private UUID id;
        private String nome;
        private String description;
        private BigDecimal price;
        private String endereco;
        private String numeroContato;
    }
} 