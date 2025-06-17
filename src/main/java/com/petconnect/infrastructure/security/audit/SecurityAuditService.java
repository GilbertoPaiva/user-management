package com.petconnect.infrastructure.security.audit;

import com.petconnect.domain.security.entity.LoginAttempt;
import com.petconnect.domain.security.entity.SecurityAuditLog;
import com.petconnect.domain.security.port.LoginAttemptRepositoryPort;
import com.petconnect.domain.security.port.SecurityAuditLogRepositoryPort;
import com.petconnect.infrastructure.security.interceptor.SecurityInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditService {
    
    private final SecurityAuditLogRepositoryPort securityAuditLogRepository;
    private final LoginAttemptRepositoryPort loginAttemptRepository;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;
    private static final int ATTEMPT_RESET_MINUTES = 15;
    
    public void recordLoginAttempt(String identifier, boolean success) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        String userAgent = SecurityInterceptor.getCurrentUserAgent();
        
        // Buscar ou criar registro de tentativa
        Optional<LoginAttempt> existingAttempt = 
            loginAttemptRepository.findByUserIdentifierAndIpAddress(identifier, clientIp);
        
        LoginAttempt attempt;
        if (existingAttempt.isPresent()) {
            attempt = existingAttempt.get();
        } else {
            attempt = LoginAttempt.create(identifier, clientIp, success);
        }
        
        if (success) {
            attempt.resetAttemptCount();
            recordSecurityEvent(SecurityAuditLog.EventType.LOGIN_SUCCESS, 
                "Login realizado com sucesso", identifier, clientIp, userAgent, true);
        } else {
            attempt.incrementAttemptCount();
            
            if (attempt.getAttemptCount() >= MAX_LOGIN_ATTEMPTS) {
                attempt.blockUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                recordSecurityEvent(SecurityAuditLog.EventType.ACCOUNT_LOCKED, 
                    "Conta bloqueada após " + attempt.getAttemptCount() + " tentativas falhas", 
                    identifier, clientIp, userAgent, false);
            } else {
                recordSecurityEvent(SecurityAuditLog.EventType.LOGIN_FAILURE,
                    "Tentativa de login falha (" + attempt.getAttemptCount() + "/" + MAX_LOGIN_ATTEMPTS + ")", 
                    identifier, clientIp, userAgent, false);
            }
        }
        
        loginAttemptRepository.save(attempt);
    }
    
    public boolean isUserBlocked(String identifier) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        Optional<LoginAttempt> attempt = 
            loginAttemptRepository.findByUserIdentifierAndIpAddress(identifier, clientIp);
        
        if (attempt.isPresent()) {
            LoginAttempt loginAttempt = attempt.get();
            
            if (loginAttempt.isBlocked()) {
                return true;
            }
            
            if (loginAttempt.getBlockedUntil() != null && 
                LocalDateTime.now().isAfter(loginAttempt.getBlockedUntil())) {
                loginAttempt.resetAttemptCount();
                loginAttemptRepository.save(loginAttempt);
            }
        }
        
        return false;
    }
    
    public boolean canAttemptLogin(String identifier) {
        if (isUserBlocked(identifier)) {
            return false;
        }
        
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        Optional<LoginAttempt> attempt = 
            loginAttemptRepository.findByUserIdentifierAndIpAddress(identifier, clientIp);
        
        if (attempt.isPresent()) {
            LoginAttempt loginAttempt = attempt.get();
            
            if (loginAttempt.getAttemptTimestamp() != null &&
                loginAttempt.getAttemptTimestamp().isBefore(
                    LocalDateTime.now().minusMinutes(ATTEMPT_RESET_MINUTES))) {
                loginAttempt.resetAttemptCount();
                loginAttemptRepository.save(loginAttempt);
                return true;
            }
            
            return loginAttempt.getAttemptCount() < MAX_LOGIN_ATTEMPTS;
        }
        
        return true;
    }
    
    public long getRemainingLockoutMinutes(String identifier) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        Optional<LoginAttempt> attempt = 
            loginAttemptRepository.findByUserIdentifierAndIpAddress(identifier, clientIp);
        
        if (attempt.isPresent()) {
            return attempt.get().getRemainingBlockMinutes();
        }
        
        return 0;
    }
    
    public boolean unlockUser(String identifier, String adminUser) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        Optional<LoginAttempt> attempt = 
            loginAttemptRepository.findByUserIdentifierAndIpAddress(identifier, clientIp);
        
        if (attempt.isPresent()) {
            LoginAttempt loginAttempt = attempt.get();
            loginAttempt.resetAttemptCount();
            loginAttemptRepository.save(loginAttempt);
            
            recordSecurityEvent(SecurityAuditLog.EventType.ACCOUNT_UNLOCKED,
                "Usuário " + identifier + " desbloqueado manualmente por " + adminUser, 
                identifier, clientIp, SecurityInterceptor.getCurrentUserAgent(), true);
            
            return true;
        }
        
        return false;
    }
    
    private void recordSecurityEvent(SecurityAuditLog.EventType eventType, String description, 
                                   String userIdentifier, String ipAddress, String userAgent, 
                                   boolean success) {
        SecurityAuditLog auditLog = SecurityAuditLog.create(eventType, description, userIdentifier, ipAddress)
                .withSuccess(success);
        
        if (userAgent != null) {
            auditLog.setUserAgent(userAgent);
        }
        
        securityAuditLogRepository.save(auditLog);
        
        log.info("Security event recorded: {} for user: {} from IP: {} - Success: {}", 
                eventType, userIdentifier, ipAddress, success);
    }
    
    public void recordUnauthorizedAccess(String operation, String details) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        String userAgent = SecurityInterceptor.getCurrentUserAgent();
        
        recordSecurityEvent(SecurityAuditLog.EventType.UNAUTHORIZED_ACCESS, 
            operation + ": " + details, "UNKNOWN", clientIp, userAgent, false);
    }
    
    public void recordSecurityViolation(String violationType, String details) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        String userAgent = SecurityInterceptor.getCurrentUserAgent();
        
        recordSecurityEvent(SecurityAuditLog.EventType.SECURITY_VIOLATION, 
            violationType + ": " + details, "UNKNOWN", clientIp, userAgent, false);
    }
    
    public void recordSensitiveOperation(String operation, String userIdentifier) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        String userAgent = SecurityInterceptor.getCurrentUserAgent();
        
        recordSecurityEvent(SecurityAuditLog.EventType.SENSITIVE_OPERATION, 
            "Operação sensível: " + operation, userIdentifier, clientIp, userAgent, true);
    }
    
    public void recordLogout(String userIdentifier) {
        String clientIp = SecurityInterceptor.getCurrentClientIp();
        String userAgent = SecurityInterceptor.getCurrentUserAgent();
        
        recordSecurityEvent(SecurityAuditLog.EventType.LOGOUT, 
            "Logout realizado", userIdentifier, clientIp, userAgent, true);
    }
}
