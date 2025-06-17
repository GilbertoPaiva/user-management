package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.security.entity.SecurityAuditLog;
import com.petconnect.domain.security.port.SecurityAuditLogRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.mapper.SecurityAuditLogMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.SecurityAuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do repositório de logs de auditoria de segurança
 */
@Repository
@RequiredArgsConstructor
@Transactional
public class SecurityAuditLogRepositoryAdapter implements SecurityAuditLogRepositoryPort {
    
    private final SecurityAuditLogJpaRepository jpaRepository;
    private final SecurityAuditLogMapper mapper;
    
    @Override
    public SecurityAuditLog save(SecurityAuditLog auditLog) {
        var jpaEntity = mapper.toJpaEntity(auditLog);
        var savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SecurityAuditLog> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findByEventType(String eventType) {
        return jpaRepository.findByEventType(eventType)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findByUserIdentifier(String userIdentifier) {
        return jpaRepository.findByUserIdentifier(userIdentifier)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findByIpAddress(String ipAddress) {
        return jpaRepository.findByIpAddress(ipAddress)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findByEventTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByEventTimestampBetween(start, end)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findByUserIdentifierAndEventTimestampBetween(
            String userIdentifier, LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByUserIdentifierAndEventTimestampBetween(userIdentifier, start, end)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findSecurityViolations(LocalDateTime since) {
        return jpaRepository.findSecurityViolations(since)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countLoginAttemptsByUserAndPeriod(String userIdentifier, LocalDateTime since) {
        return jpaRepository.countLoginAttemptsByUserAndPeriod(userIdentifier, since);
    }
    
    @Override
    public void deleteLogsOlderThan(LocalDateTime cutoffDate) {
        jpaRepository.deleteByEventTimestampBefore(cutoffDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findAll() {
        return jpaRepository.findAllOrderByEventTimestampDesc()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
