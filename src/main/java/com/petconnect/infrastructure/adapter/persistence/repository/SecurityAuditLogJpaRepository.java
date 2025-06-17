package com.petconnect.infrastructure.adapter.persistence.repository;

import com.petconnect.infrastructure.adapter.persistence.entity.SecurityAuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositório JPA para logs de auditoria de segurança
 */
@Repository
public interface SecurityAuditLogJpaRepository extends JpaRepository<SecurityAuditLogJpaEntity, UUID> {
    
    List<SecurityAuditLogJpaEntity> findByEventType(String eventType);
    
    List<SecurityAuditLogJpaEntity> findByUserIdentifier(String userIdentifier);
    
    List<SecurityAuditLogJpaEntity> findByIpAddress(String ipAddress);
    
    List<SecurityAuditLogJpaEntity> findByEventTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<SecurityAuditLogJpaEntity> findByUserIdentifierAndEventTimestampBetween(
            String userIdentifier, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT s FROM SecurityAuditLogJpaEntity s WHERE s.success = false AND s.eventTimestamp >= :since")
    List<SecurityAuditLogJpaEntity> findSecurityViolations(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(s) FROM SecurityAuditLogJpaEntity s WHERE s.userIdentifier = :userIdentifier " +
           "AND s.eventType LIKE 'LOGIN%' AND s.eventTimestamp >= :since")
    long countLoginAttemptsByUserAndPeriod(@Param("userIdentifier") String userIdentifier, 
                                         @Param("since") LocalDateTime since);
    
    void deleteByEventTimestampBefore(LocalDateTime cutoffDate);
    
    @Query("SELECT s FROM SecurityAuditLogJpaEntity s ORDER BY s.eventTimestamp DESC")
    List<SecurityAuditLogJpaEntity> findAllOrderByEventTimestampDesc();
}
