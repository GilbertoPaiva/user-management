package com.petconnect.domain.security.entity;

import com.petconnect.domain.shared.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SecurityAuditLog extends AuditableEntity {
    
    private String eventType;
    private String eventDescription;
    private String userIdentifier;
    private String ipAddress;
    private String userAgent;
    private Boolean success;
    private LocalDateTime eventTimestamp;
    private String additionalData;
    
    public enum EventType {
        LOGIN_ATTEMPT("LOGIN_ATTEMPT"),
        LOGIN_SUCCESS("LOGIN_SUCCESS"),
        LOGIN_FAILURE("LOGIN_FAILURE"),
        LOGOUT("LOGOUT"),
        PASSWORD_CHANGE("PASSWORD_CHANGE"),
        ACCOUNT_LOCKED("ACCOUNT_LOCKED"),
        ACCOUNT_UNLOCKED("ACCOUNT_UNLOCKED"),
        UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS"),
        SECURITY_VIOLATION("SECURITY_VIOLATION"),
        TOKEN_REFRESH("TOKEN_REFRESH"),
        DATA_ACCESS("DATA_ACCESS"),
        SENSITIVE_OPERATION("SENSITIVE_OPERATION");
        
        private final String value;
        
        EventType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Método estático para criar um log de auditoria básico
     */
    public static SecurityAuditLog create(EventType eventType, String description, 
                                        String userIdentifier, String ipAddress) {
        return SecurityAuditLog.builder()
                .id(UUID.randomUUID())
                .eventType(eventType.getValue())
                .eventDescription(description)
                .userIdentifier(userIdentifier)
                .ipAddress(ipAddress)
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Método para adicionar dados adicionais como JSON
     */
    public SecurityAuditLog withAdditionalData(String data) {
        this.additionalData = data;
        return this;
    }
    
    /**
     * Método para marcar como sucesso/falha
     */
    public SecurityAuditLog withSuccess(Boolean success) {
        this.success = success;
        return this;
    }
}
