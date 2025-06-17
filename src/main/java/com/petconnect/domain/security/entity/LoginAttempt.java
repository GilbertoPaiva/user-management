package com.petconnect.domain.security.entity;

import com.petconnect.domain.shared.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade para controle de tentativas de login
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginAttempt extends AuditableEntity {
    
    private String userIdentifier;
    private String ipAddress;
    private LocalDateTime attemptTimestamp;
    private Boolean success;
    private LocalDateTime blockedUntil;
    private Integer attemptCount;
    
    /**
     * Método estático para criar uma tentativa de login
     */
    public static LoginAttempt create(String userIdentifier, String ipAddress, Boolean success) {
        return LoginAttempt.builder()
                .id(UUID.randomUUID())
                .userIdentifier(userIdentifier)
                .ipAddress(ipAddress)
                .success(success)
                .attemptTimestamp(LocalDateTime.now())
                .attemptCount(success ? 0 : 1)
                .build();
    }
    
    /**
     * Incrementa o contador de tentativas
     */
    public void incrementAttemptCount() {
        this.attemptCount = (this.attemptCount == null ? 0 : this.attemptCount) + 1;
    }
    
    /**
     * Reseta o contador de tentativas (após login bem-sucedido)
     */
    public void resetAttemptCount() {
        this.attemptCount = 0;
        this.blockedUntil = null;
    }
    
    /**
     * Bloqueia o usuário por um período
     */
    public void blockUntil(LocalDateTime until) {
        this.blockedUntil = until;
    }
    
    /**
     * Verifica se ainda está bloqueado
     */
    public boolean isBlocked() {
        return blockedUntil != null && LocalDateTime.now().isBefore(blockedUntil);
    }
    
    /**
     * Calcula quantos minutos restam do bloqueio
     */
    public long getRemainingBlockMinutes() {
        if (!isBlocked()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), blockedUntil).toMinutes();
    }
}
