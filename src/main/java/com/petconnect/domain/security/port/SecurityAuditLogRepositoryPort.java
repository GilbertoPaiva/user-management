package com.petconnect.domain.security.port;

import com.petconnect.domain.security.entity.SecurityAuditLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port para repositório de logs de auditoria de segurança
 */
public interface SecurityAuditLogRepositoryPort {
    
    /**
     * Salva um log de auditoria
     */
    SecurityAuditLog save(SecurityAuditLog auditLog);
    
    /**
     * Busca um log por ID
     */
    Optional<SecurityAuditLog> findById(UUID id);
    
    /**
     * Busca logs por tipo de evento
     */
    List<SecurityAuditLog> findByEventType(String eventType);
    
    /**
     * Busca logs por usuário
     */
    List<SecurityAuditLog> findByUserIdentifier(String userIdentifier);
    
    /**
     * Busca logs por IP
     */
    List<SecurityAuditLog> findByIpAddress(String ipAddress);
    
    /**
     * Busca logs por período
     */
    List<SecurityAuditLog> findByEventTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Busca logs por usuário e período
     */
    List<SecurityAuditLog> findByUserIdentifierAndEventTimestampBetween(
            String userIdentifier, LocalDateTime start, LocalDateTime end);
    
    /**
     * Busca logs de falhas de segurança
     */
    List<SecurityAuditLog> findSecurityViolations(LocalDateTime since);
    
    /**
     * Conta tentativas de login por usuário e período
     */
    long countLoginAttemptsByUserAndPeriod(String userIdentifier, LocalDateTime since);
    
    /**
     * Remove logs antigos (para limpeza periódica)
     */
    void deleteLogsOlderThan(LocalDateTime cutoffDate);
    
    /**
     * Busca todos os logs
     */
    List<SecurityAuditLog> findAll();
}
