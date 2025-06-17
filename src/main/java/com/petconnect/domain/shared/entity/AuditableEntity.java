package com.petconnect.domain.shared.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class AuditableEntity {
    protected UUID id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected String createdBy;
    protected String updatedBy;
    
    public void markAsCreated(String createdBy) {
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.updatedAt = this.createdAt;
        this.updatedBy = createdBy;
    }
    
    public void markAsUpdated(String updatedBy) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }
}
