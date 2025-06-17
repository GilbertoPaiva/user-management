package com.petconnect.infrastructure.adapter.persistence.entity;

import com.petconnect.domain.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Entidade JPA para logs de auditoria de segurança
 */
@Entity
@Table(name = "security_audit_logs", 
       indexes = {
           @Index(name = "idx_security_audit_event_type", columnList = "event_type"),
           @Index(name = "idx_security_audit_user", columnList = "user_identifier"),
           @Index(name = "idx_security_audit_timestamp", columnList = "event_timestamp"),
           @Index(name = "idx_security_audit_ip", columnList = "ip_address")
       })
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SecurityAuditLogJpaEntity extends AuditableEntity {
    
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;
    
    @Column(name = "user_identifier")
    private String userIdentifier;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "success")
    private Boolean success;
    
    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;
    
    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;
    
    @PrePersist
    public void prePersist() {
        if (eventTimestamp == null) {
            eventTimestamp = LocalDateTime.now();
        }
    }
}
