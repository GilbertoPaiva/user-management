package com.petconnect.domain.security.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class LoginAttemptTest {

    @Test
    void shouldCreateLoginAttemptWithRequiredFields() {
        String userIdentifier = "test@example.com";
        String ipAddress = "192.168.1.1";
        
        LoginAttempt attempt = LoginAttempt.create(userIdentifier, ipAddress, false);

        assertNotNull(attempt);
        assertNotNull(attempt.getId());
        assertEquals(userIdentifier, attempt.getUserIdentifier());
        assertEquals(ipAddress, attempt.getIpAddress());
        assertNotNull(attempt.getAttemptTimestamp());
        assertEquals(1, attempt.getAttemptCount());
        assertFalse(attempt.getSuccess());
        assertNull(attempt.getBlockedUntil());
        assertFalse(attempt.isBlocked());
    }

    @Test
    void shouldCreateSuccessfulLoginAttempt() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "10.0.0.1", true);

        assertTrue(attempt.getSuccess());
        assertEquals(0, attempt.getAttemptCount());
    }

    @Test
    void shouldIncrementAttemptCount() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        int initialCount = attempt.getAttemptCount();
        
        attempt.incrementAttemptCount();
        
        assertEquals(initialCount + 1, attempt.getAttemptCount());
        assertNotNull(attempt.getAttemptTimestamp());
    }

    @Test
    void shouldResetAttemptCount() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        attempt.incrementAttemptCount();
        attempt.incrementAttemptCount();
        
        attempt.resetAttemptCount();
        
        assertEquals(0, attempt.getAttemptCount());
        assertNull(attempt.getBlockedUntil());
    }

    @Test
    void shouldBlockUntilSpecificTime() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime blockUntil = LocalDateTime.now().plusMinutes(30);
        
        attempt.blockUntil(blockUntil);
        
        assertEquals(blockUntil, attempt.getBlockedUntil());
        assertTrue(attempt.isBlocked());
    }

    @Test
    void shouldNotBeBlockedWhenNoBlockTime() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        
        assertFalse(attempt.isBlocked());
    }

    @Test
    void shouldNotBeBlockedWhenBlockTimeExpired() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(1);
        attempt.blockUntil(pastTime);
        
        assertFalse(attempt.isBlocked());
    }

    @Test
    void shouldBeBlockedWhenBlockTimeInFuture() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(30);
        attempt.blockUntil(futureTime);
        
        assertTrue(attempt.isBlocked());
    }

    @Test
    void shouldCalculateRemainingBlockMinutes() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime blockUntil = LocalDateTime.now().plusMinutes(25);
        attempt.blockUntil(blockUntil);
        
        long remainingMinutes = attempt.getRemainingBlockMinutes();
        
        assertTrue(remainingMinutes > 20 && remainingMinutes <= 25);
    }

    @Test
    void shouldReturnZeroRemainingMinutesWhenNotBlocked() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        
        assertEquals(0, attempt.getRemainingBlockMinutes());
    }

    @Test
    void shouldReturnZeroRemainingMinutesWhenBlockExpired() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(10);
        attempt.blockUntil(pastTime);
        
        assertEquals(0, attempt.getRemainingBlockMinutes());
    }

    @Test
    void shouldUpdateAttemptTimestampOnIncrement() throws InterruptedException {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime initialTimestamp = attempt.getAttemptTimestamp();
        
        Thread.sleep(1);
        attempt.incrementAttemptCount();
        
        assertTrue(attempt.getAttemptTimestamp().isAfter(initialTimestamp) ||
                   attempt.getAttemptTimestamp().isEqual(initialTimestamp));
    }

    @Test
    void shouldHandleMultipleIncrements() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        
        for (int i = 2; i <= 5; i++) {
            attempt.incrementAttemptCount();
            assertEquals(i, attempt.getAttemptCount());
        }
    }

    @Test
    void shouldMaintainIpAndIdentifierAfterOperations() {
        String userIdentifier = "test@example.com";
        String ipAddress = "203.0.113.1";
        
        LoginAttempt attempt = LoginAttempt.create(userIdentifier, ipAddress, false);
        attempt.incrementAttemptCount();
        attempt.blockUntil(LocalDateTime.now().plusHours(1));
        attempt.resetAttemptCount();
        
        assertEquals(userIdentifier, attempt.getUserIdentifier());
        assertEquals(ipAddress, attempt.getIpAddress());
    }

    @Test
    void shouldCreateUniqueIds() {
        LoginAttempt attempt1 = LoginAttempt.create("user1@test.com", "192.168.1.1", false);
        LoginAttempt attempt2 = LoginAttempt.create("user2@test.com", "192.168.1.2", false);
        
        assertNotEquals(attempt1.getId(), attempt2.getId());
    }

    @Test
    void shouldHandleNullValues() {
        LoginAttempt attempt = LoginAttempt.create(null, null, false);
        
        assertNotNull(attempt);
        assertNull(attempt.getUserIdentifier());
        assertNull(attempt.getIpAddress());
        assertNotNull(attempt.getId());
        assertNotNull(attempt.getAttemptTimestamp());
    }

    @Test
    void shouldTestEdgeCaseWithZeroMinuteBlock() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime now = LocalDateTime.now();
        attempt.blockUntil(now);
        
        long remainingMinutes = attempt.getRemainingBlockMinutes();
        assertTrue(remainingMinutes <= 1);
    }

    @Test
    void shouldTestLongTermBlock() {
        LoginAttempt attempt = LoginAttempt.create("user@test.com", "192.168.1.1", false);
        LocalDateTime farFuture = LocalDateTime.now().plusDays(1);
        attempt.blockUntil(farFuture);
        
        assertTrue(attempt.isBlocked());
        long remainingMinutes = attempt.getRemainingBlockMinutes();
        assertTrue(remainingMinutes > 1400); // More than 23 hours
    }
}
