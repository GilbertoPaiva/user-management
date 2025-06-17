package com.petconnect.domain.shared.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;
    
    @Column(name = "created_by", updatable = false, length = 100)
    protected String createdBy;
    
    @Column(name = "updated_by", length = 100)
    protected String updatedBy;
    
    @Column(name = "version", nullable = false)
    @Version
    @Builder.Default
    protected Long version = 0L;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUser();
        
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = currentUser;
        this.updatedBy = currentUser;
    }
    
    @PreUpdate
    protected void onUpdate() {
        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUser();
        
        this.updatedAt = now;
        this.updatedBy = currentUser;
    }
    
    private String getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getName())) {
                return "system";
            }
            return authentication.getName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
