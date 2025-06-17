package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.security.entity.SecurityAuditLog;
import com.petconnect.infrastructure.adapter.persistence.entity.SecurityAuditLogJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para SecurityAuditLog
 */
@Component
public class SecurityAuditLogMapper {
    
    /**
     * Converte de domínio para JPA
     */
    public SecurityAuditLogJpaEntity toJpaEntity(SecurityAuditLog domain) {
        if (domain == null) {
            return null;
        }
        
        return SecurityAuditLogJpaEntity.builder()
                .id(domain.getId())
                .eventType(domain.getEventType())
                .eventDescription(domain.getEventDescription())
                .userIdentifier(domain.getUserIdentifier())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .success(domain.getSuccess())
                .eventTimestamp(domain.getEventTimestamp())
                .additionalData(domain.getAdditionalData())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(domain.getVersion())
                .build();
    }
    
    /**
     * Converte de JPA para domínio
     */
    public SecurityAuditLog toDomain(SecurityAuditLogJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return SecurityAuditLog.builder()
                .id(jpaEntity.getId())
                .eventType(jpaEntity.getEventType())
                .eventDescription(jpaEntity.getEventDescription())
                .userIdentifier(jpaEntity.getUserIdentifier())
                .ipAddress(jpaEntity.getIpAddress())
                .userAgent(jpaEntity.getUserAgent())
                .success(jpaEntity.getSuccess())
                .eventTimestamp(jpaEntity.getEventTimestamp())
                .additionalData(jpaEntity.getAdditionalData())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .version(jpaEntity.getVersion())
                .build();
    }
}
