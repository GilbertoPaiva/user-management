package com.petconnect.domain.security.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityAuditLogTest {

    @Test
    void shouldCreateSecurityAuditLogWithRequiredFields() {
        String userIdentifier = "test@example.com";
        String ipAddress = "192.168.1.1";
        String description = "Login attempt";
        
        SecurityAuditLog auditLog = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_ATTEMPT, 
            description, 
            userIdentifier, 
            ipAddress
        );

        assertNotNull(auditLog);
        assertNotNull(auditLog.getId());
        assertEquals(SecurityAuditLog.EventType.LOGIN_ATTEMPT.getValue(), auditLog.getEventType());
        assertEquals(description, auditLog.getEventDescription());
        assertEquals(userIdentifier, auditLog.getUserIdentifier());
        assertEquals(ipAddress, auditLog.getIpAddress());
        assertNotNull(auditLog.getEventTimestamp());
        assertNull(auditLog.getSuccess());
    }

    @Test
    void shouldCreateWithSuccessFlag() {
        SecurityAuditLog auditLog = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_SUCCESS, 
            "Successful login", 
            "user@test.com", 
            "10.0.0.1"
        ).withSuccess(true);

        assertTrue(auditLog.getSuccess());
    }

    @Test
    void shouldSetUserAgent() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
        
        SecurityAuditLog auditLog = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_ATTEMPT, 
            "Login", 
            "user@test.com", 
            "192.168.1.1"
        );
        
        auditLog.setUserAgent(userAgent);
        assertEquals(userAgent, auditLog.getUserAgent());
    }

    @Test
    void shouldSetAdditionalData() {
        String additionalData = "{\"browser\": \"Chrome\", \"os\": \"Windows\"}";
        
        SecurityAuditLog auditLog = SecurityAuditLog.create(
            SecurityAuditLog.EventType.SECURITY_VIOLATION, 
            "Suspicious activity", 
            "user@test.com", 
            "192.168.1.1"
        );
        
        auditLog.setAdditionalData(additionalData);
        assertEquals(additionalData, auditLog.getAdditionalData());
    }

    @Test
    void shouldHandleAllEventTypes() {
        SecurityAuditLog.EventType[] eventTypes = SecurityAuditLog.EventType.values();
        
        for (SecurityAuditLog.EventType eventType : eventTypes) {
            SecurityAuditLog auditLog = SecurityAuditLog.create(
                eventType, 
                "Test event for " + eventType, 
                "user@test.com", 
                "192.168.1.1"
            );
            
            assertEquals(eventType.getValue(), auditLog.getEventType());
            assertTrue(auditLog.getEventDescription().contains(eventType.toString()));
        }
    }

    @Test
    void shouldMaintainTimestampOrder() throws InterruptedException {
        SecurityAuditLog log1 = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_ATTEMPT, 
            "First event", 
            "user@test.com", 
            "192.168.1.1"
        );
        
        Thread.sleep(1);
        
        SecurityAuditLog log2 = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_SUCCESS, 
            "Second event", 
            "user@test.com", 
            "192.168.1.1"
        );

        assertTrue(log2.getEventTimestamp().isAfter(log1.getEventTimestamp()) ||
                   log2.getEventTimestamp().isEqual(log1.getEventTimestamp()));
    }

    @Test
    void shouldCreateLogWithCompleteInformation() {
        String userIdentifier = "admin@petconnect.com";
        String ipAddress = "203.0.113.1";
        String description = "Administrative action performed";
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)";
        String additionalData = "{\"action\": \"user_unlock\", \"targetUser\": \"blocked@test.com\"}";
        
        SecurityAuditLog auditLog = SecurityAuditLog.create(
            SecurityAuditLog.EventType.SENSITIVE_OPERATION, 
            description, 
            userIdentifier, 
            ipAddress
        ).withSuccess(true);
        
        auditLog.setUserAgent(userAgent);
        auditLog.setAdditionalData(additionalData);

        assertEquals(SecurityAuditLog.EventType.SENSITIVE_OPERATION.getValue(), auditLog.getEventType());
        assertEquals(description, auditLog.getEventDescription());
        assertEquals(userIdentifier, auditLog.getUserIdentifier());
        assertEquals(ipAddress, auditLog.getIpAddress());
        assertEquals(userAgent, auditLog.getUserAgent());
        assertEquals(additionalData, auditLog.getAdditionalData());
        assertTrue(auditLog.getSuccess());
        assertNotNull(auditLog.getEventTimestamp());
        assertNotNull(auditLog.getId());
    }

    @Test
    void shouldHandleNullValues() {
        SecurityAuditLog auditLog = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_ATTEMPT, 
            "Test", 
            null, 
            null
        );

        assertNotNull(auditLog);
        assertNull(auditLog.getUserIdentifier());
        assertNull(auditLog.getIpAddress());
        assertNull(auditLog.getUserAgent());
        assertNull(auditLog.getAdditionalData());
    }

    @Test
    void shouldCreateUniqueIds() {
        SecurityAuditLog log1 = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_ATTEMPT, 
            "Event 1", 
            "user1@test.com", 
            "192.168.1.1"
        );
        
        SecurityAuditLog log2 = SecurityAuditLog.create(
            SecurityAuditLog.EventType.LOGIN_ATTEMPT, 
            "Event 2", 
            "user2@test.com", 
            "192.168.1.2"
        );

        assertNotEquals(log1.getId(), log2.getId());
    }

    @Test
    void shouldTestEventTypeEnumValues() {
        SecurityAuditLog.EventType[] expectedTypes = {
            SecurityAuditLog.EventType.LOGIN_ATTEMPT,
            SecurityAuditLog.EventType.LOGIN_SUCCESS,
            SecurityAuditLog.EventType.LOGIN_FAILURE,
            SecurityAuditLog.EventType.LOGOUT,
            SecurityAuditLog.EventType.ACCOUNT_LOCKED,
            SecurityAuditLog.EventType.ACCOUNT_UNLOCKED,
            SecurityAuditLog.EventType.PASSWORD_CHANGE,
            SecurityAuditLog.EventType.UNAUTHORIZED_ACCESS,
            SecurityAuditLog.EventType.SECURITY_VIOLATION,
            SecurityAuditLog.EventType.SENSITIVE_OPERATION,
            SecurityAuditLog.EventType.TOKEN_REFRESH,
            SecurityAuditLog.EventType.DATA_ACCESS
        };

        SecurityAuditLog.EventType[] actualTypes = SecurityAuditLog.EventType.values();
        
        for (SecurityAuditLog.EventType expected : expectedTypes) {
            boolean found = false;
            for (SecurityAuditLog.EventType actual : actualTypes) {
                if (expected == actual) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "EventType " + expected + " should exist");
        }
    }
}
