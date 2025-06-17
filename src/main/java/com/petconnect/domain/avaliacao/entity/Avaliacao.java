package com.petconnect.domain.avaliacao.entity;

import com.petconnect.domain.shared.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao extends AuditableEntity {
    
    private UUID entidadeId;
    private TipoEntidade tipoEntidade;
    private UUID autorId;
    private String autorNome;
    private int nota;
    private String comentario;
    private boolean ativo;
    
    public enum TipoEntidade {
        PRODUTO, SERVICO, VETERINARIO, LOJISTA
    }
    
    public void validarNota() {
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("Nota deve estar entre 1 e 5");
        }
    }
    
    public void desativar() {
        this.ativo = false;
    }
    
    public void ativar() {
        this.ativo = true;
    }
    
    public boolean isNotaExcelente() {
        return nota >= 4;
    }
    
    public boolean isNotaRuim() {
        return nota <= 2;
    }
}
