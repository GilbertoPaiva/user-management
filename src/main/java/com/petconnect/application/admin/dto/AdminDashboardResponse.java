package com.petconnect.application.admin.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class AdminDashboardResponse {
    private long totalAdmins;
    private long totalTutores;
    private long totalVeterinarios;
    private long totalLojistas;
    private List<UserSummary> usuarios;
    private UserDetail usuarioDetalhe;

    @Data
    public static class UserSummary {
        private UUID id;
        private String nome;
        private String email;
        private String tipo;
    }

    @Data
    public static class UserDetail {
        private UUID id;
        private String nome;
        private String email;
        private String tipo;
        private Object dadosCadastrais;
        private List<Object> produtos;
        private List<Object> servicos;
        private List<Object> animais;
    }
} 