package com.petconnect.domain.security.port;

import com.petconnect.domain.security.entity.LoginAttempt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port para repositório de tentativas de login
 */
public interface LoginAttemptRepositoryPort {
    
    /**
     * Salva uma tentativa de login
     */
    LoginAttempt save(LoginAttempt loginAttempt);
    
    /**
     * Busca uma tentativa por ID
     */
    Optional<LoginAttempt> findById(UUID id);
    
    /**
     * Busca tentativa por usuário e IP
     */
    Optional<LoginAttempt> findByUserIdentifierAndIpAddress(String userIdentifier, String ipAddress);
    
    /**
     * Busca tentativas por usuário
     */
    List<LoginAttempt> findByUserIdentifier(String userIdentifier);
    
    /**
     * Busca tentativas por IP
     */
    List<LoginAttempt> findByIpAddress(String ipAddress);
    
    /**
     * Busca tentativas recentes por usuário
     */
    List<LoginAttempt> findRecentAttemptsByUser(String userIdentifier, LocalDateTime since);
    
    /**
     * Busca tentativas bloqueadas
     */
    List<LoginAttempt> findBlockedAttempts();
    
    /**
     * Busca tentativas que devem ser desbloqueadas
     */
    List<LoginAttempt> findAttemptsToUnblock();
    
    /**
     * Conta tentativas falhadas por usuário e período
     */
    long countFailedAttemptsByUserAndPeriod(String userIdentifier, LocalDateTime since);
    
    /**
     * Conta tentativas falhadas por IP e período
     */
    long countFailedAttemptsByIpAndPeriod(String ipAddress, LocalDateTime since);
    
    /**
     * Remove tentativas antigas (para limpeza periódica)
     */
    void deleteAttemptsOlderThan(LocalDateTime cutoffDate);
    
    /**
     * Remove tentativas por usuário
     */
    void deleteByUserIdentifier(String userIdentifier);
    
    /**
     * Busca todas as tentativas
     */
    List<LoginAttempt> findAll();
}
