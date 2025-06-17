package com.petconnect.infrastructure.adapter.persistence.mapper;

import com.petconnect.domain.security.entity.LoginAttempt;
import com.petconnect.infrastructure.adapter.persistence.entity.LoginAttemptJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para LoginAttempt
 */
@Component
public class LoginAttemptMapper {
    
    /**
     * Converte de domínio para JPA
     */
    public LoginAttemptJpaEntity toJpaEntity(LoginAttempt domain) {
        if (domain == null) {
            return null;
        }
        
        return LoginAttemptJpaEntity.builder()
                .id(domain.getId())
                .userIdentifier(domain.getUserIdentifier())
                .ipAddress(domain.getIpAddress())
                .attemptTimestamp(domain.getAttemptTimestamp())
                .success(domain.getSuccess())
                .blockedUntil(domain.getBlockedUntil())
                .attemptCount(domain.getAttemptCount())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(domain.getVersion())
                .build();
    }
    
    /**
     * Converte de JPA para domínio
     */
    public LoginAttempt toDomain(LoginAttemptJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return LoginAttempt.builder()
                .id(jpaEntity.getId())
                .userIdentifier(jpaEntity.getUserIdentifier())
                .ipAddress(jpaEntity.getIpAddress())
                .attemptTimestamp(jpaEntity.getAttemptTimestamp())
                .success(jpaEntity.getSuccess())
                .blockedUntil(jpaEntity.getBlockedUntil())
                .attemptCount(jpaEntity.getAttemptCount())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .version(jpaEntity.getVersion())
                .build();
    }
}
