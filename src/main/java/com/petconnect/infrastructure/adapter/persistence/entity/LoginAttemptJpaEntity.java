package com.petconnect.infrastructure.adapter.persistence.entity;

import com.petconnect.domain.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts",
       indexes = {
           @Index(name = "idx_login_attempts_user", columnList = "user_identifier"),
           @Index(name = "idx_login_attempts_ip", columnList = "ip_address"),
           @Index(name = "idx_login_attempts_timestamp", columnList = "attempt_timestamp")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_login_attempts_user_ip", 
                           columnNames = {"user_identifier", "ip_address"})
       })
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginAttemptJpaEntity extends AuditableEntity {
    
    @Column(name = "user_identifier", nullable = false)
    private String userIdentifier;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "attempt_timestamp")
    private LocalDateTime attemptTimestamp;
    
    @Column(name = "success")
    private Boolean success;
    
    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;
    
    @Column(name = "attempt_count")
    private Integer attemptCount;
    
    @PrePersist
    public void prePersist() {
        if (attemptTimestamp == null) {
            attemptTimestamp = LocalDateTime.now();
        }
        if (attemptCount == null) {
            attemptCount = success != null && success ? 0 : 1;
        }
    }
}
