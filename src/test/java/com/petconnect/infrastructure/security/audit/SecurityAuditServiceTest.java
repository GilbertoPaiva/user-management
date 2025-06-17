package com.petconnect.infrastructure.security.audit;

import com.petconnect.domain.security.entity.LoginAttempt;
import com.petconnect.domain.security.entity.SecurityAuditLog;
import com.petconnect.domain.security.port.LoginAttemptRepositoryPort;
import com.petconnect.domain.security.port.SecurityAuditLogRepositoryPort;
import com.petconnect.infrastructure.security.interceptor.SecurityInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAuditServiceTest {

    @Mock
    private SecurityAuditLogRepositoryPort securityAuditLogRepository;

    @Mock
    private LoginAttemptRepositoryPort loginAttemptRepository;

    @InjectMocks
    private SecurityAuditService securityAuditService;

    private static final String TEST_IDENTIFIER = "test@example.com";
    private static final String TEST_IP = "192.168.1.1";
    private static final String TEST_USER_AGENT = "Mozilla/5.0";

    @BeforeEach
    void setUp() {
        reset(securityAuditLogRepository, loginAttemptRepository);
    }

    @Test
    void shouldRecordSuccessfulLoginAttempt() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            LoginAttempt existingAttempt = LoginAttempt.create(TEST_IDENTIFIER, TEST_IP, false);
            existingAttempt.incrementAttemptCount();
            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.of(existingAttempt));

            securityAuditService.recordLoginAttempt(TEST_IDENTIFIER, true);

            verify(loginAttemptRepository).save(argThat(attempt -> 
                attempt.getAttemptCount() == 0 && !attempt.isBlocked()));
            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.LOGIN_SUCCESS.getValue().equals(log.getEventType())));
        }
    }

    @Test
    void shouldRecordFailedLoginAttempt() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.empty());

            securityAuditService.recordLoginAttempt(TEST_IDENTIFIER, false);

            verify(loginAttemptRepository).save(argThat(attempt -> 
                attempt.getAttemptCount() == 1 && !attempt.isBlocked()));
            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.LOGIN_FAILURE.getValue().equals(log.getEventType())));
        }
    }

    @Test
    void shouldBlockUserAfterMaxFailedAttempts() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            LoginAttempt existingAttempt = LoginAttempt.create(TEST_IDENTIFIER, TEST_IP, false);
            for (int i = 0; i < 3; i++) {
                existingAttempt.incrementAttemptCount();
            }
            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.of(existingAttempt));

            securityAuditService.recordLoginAttempt(TEST_IDENTIFIER, false);

            verify(loginAttemptRepository).save(argThat(attempt -> 
                attempt.getAttemptCount() == 5 && attempt.isBlocked()));
            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.ACCOUNT_LOCKED.getValue().equals(log.getEventType())));
        }
    }

    @Test
    void shouldReturnTrueWhenUserIsBlocked() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);

            LoginAttempt blockedAttempt = LoginAttempt.create(TEST_IDENTIFIER, TEST_IP, false);
            blockedAttempt.blockUntil(LocalDateTime.now().plusMinutes(30));
            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.of(blockedAttempt));

            boolean isBlocked = securityAuditService.isUserBlocked(TEST_IDENTIFIER);

            assertTrue(isBlocked);
        }
    }

    @Test
    void shouldReturnFalseWhenUserIsNotBlocked() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);

            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.empty());

            boolean isBlocked = securityAuditService.isUserBlocked(TEST_IDENTIFIER);

            assertFalse(isBlocked);
        }
    }

    @Test
    void shouldUnblockExpiredUser() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);

            LoginAttempt expiredBlocked = LoginAttempt.create(TEST_IDENTIFIER, TEST_IP, false);
            expiredBlocked.blockUntil(LocalDateTime.now().minusMinutes(1));
            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.of(expiredBlocked));

            boolean isBlocked = securityAuditService.isUserBlocked(TEST_IDENTIFIER);

            assertFalse(isBlocked);
            verify(loginAttemptRepository).save(argThat(attempt -> 
                attempt.getAttemptCount() == 0 && !attempt.isBlocked()));
        }
    }

    @Test
    void shouldAllowLoginWhenUnderAttemptLimit() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);

            LoginAttempt attempt = LoginAttempt.create(TEST_IDENTIFIER, TEST_IP, false);
            attempt.incrementAttemptCount();
            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.of(attempt));

            boolean canAttempt = securityAuditService.canAttemptLogin(TEST_IDENTIFIER);

            assertTrue(canAttempt);
        }
    }

    @Test
    void shouldResetAttemptsAfterResetTime() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);

            LoginAttempt oldAttempt = LoginAttempt.create(TEST_IDENTIFIER, TEST_IP, false);
            oldAttempt.setAttemptTimestamp(LocalDateTime.now().minusMinutes(20));
            oldAttempt.incrementAttemptCount();
            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.of(oldAttempt));

            boolean canAttempt = securityAuditService.canAttemptLogin(TEST_IDENTIFIER);

            assertTrue(canAttempt);
            verify(loginAttemptRepository).save(argThat(attempt -> 
                attempt.getAttemptCount() == 0));
        }
    }

    @Test
    void shouldUnlockUserManually() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            LoginAttempt blockedAttempt = LoginAttempt.create(TEST_IDENTIFIER, TEST_IP, false);
            blockedAttempt.blockUntil(LocalDateTime.now().plusMinutes(30));
            when(loginAttemptRepository.findByUserIdentifierAndIpAddress(TEST_IDENTIFIER, TEST_IP))
                    .thenReturn(Optional.of(blockedAttempt));

            boolean unlocked = securityAuditService.unlockUser(TEST_IDENTIFIER, "admin");

            assertTrue(unlocked);
            verify(loginAttemptRepository).save(argThat(attempt -> 
                attempt.getAttemptCount() == 0 && !attempt.isBlocked()));
            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.ACCOUNT_UNLOCKED.getValue().equals(log.getEventType())));
        }
    }

    @Test
    void shouldRecordUnauthorizedAccess() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            securityAuditService.recordUnauthorizedAccess("API_ACCESS", "Invalid token");

            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.UNAUTHORIZED_ACCESS.getValue().equals(log.getEventType()) &&
                log.getEventDescription().contains("API_ACCESS: Invalid token")));
        }
    }

    @Test
    void shouldRecordSecurityViolation() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            securityAuditService.recordSecurityViolation("SQL_INJECTION", "Malicious query detected");

            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.SECURITY_VIOLATION.getValue().equals(log.getEventType()) &&
                log.getEventDescription().contains("SQL_INJECTION: Malicious query detected")));
        }
    }

    @Test
    void shouldRecordSensitiveOperation() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            securityAuditService.recordSensitiveOperation("PASSWORD_CHANGE", TEST_IDENTIFIER);

            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.SENSITIVE_OPERATION.getValue().equals(log.getEventType()) &&
                log.getEventDescription().contains("Operação sensível: PASSWORD_CHANGE")));
        }
    }

    @Test
    void shouldRecordLogout() {
        try (MockedStatic<SecurityInterceptor> mockedInterceptor = mockStatic(SecurityInterceptor.class)) {
            mockedInterceptor.when(SecurityInterceptor::getCurrentClientIp).thenReturn(TEST_IP);
            mockedInterceptor.when(SecurityInterceptor::getCurrentUserAgent).thenReturn(TEST_USER_AGENT);

            securityAuditService.recordLogout(TEST_IDENTIFIER);

            verify(securityAuditLogRepository).save(argThat(log -> 
                SecurityAuditLog.EventType.LOGOUT.getValue().equals(log.getEventType()) &&
                log.getEventDescription().equals("Logout realizado")));
        }
    }
}
