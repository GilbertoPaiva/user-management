package com.petconnect.infrastructure.adapter.persistence.repository;

import com.petconnect.infrastructure.adapter.persistence.entity.LoginAttemptJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA para tentativas de login
 */
@Repository
public interface LoginAttemptJpaRepository extends JpaRepository<LoginAttemptJpaEntity, UUID> {
    
    Optional<LoginAttemptJpaEntity> findByUserIdentifierAndIpAddress(String userIdentifier, String ipAddress);
    
    List<LoginAttemptJpaEntity> findByUserIdentifier(String userIdentifier);
    
    List<LoginAttemptJpaEntity> findByIpAddress(String ipAddress);
    
    @Query("SELECT l FROM LoginAttemptJpaEntity l WHERE l.userIdentifier = :userIdentifier " +
           "AND l.attemptTimestamp >= :since ORDER BY l.attemptTimestamp DESC")
    List<LoginAttemptJpaEntity> findRecentAttemptsByUser(@Param("userIdentifier") String userIdentifier, 
                                                       @Param("since") LocalDateTime since);
    
    @Query("SELECT l FROM LoginAttemptJpaEntity l WHERE l.blockedUntil IS NOT NULL " +
           "AND l.blockedUntil > CURRENT_TIMESTAMP")
    List<LoginAttemptJpaEntity> findBlockedAttempts();
    
    @Query("SELECT l FROM LoginAttemptJpaEntity l WHERE l.blockedUntil IS NOT NULL " +
           "AND l.blockedUntil <= CURRENT_TIMESTAMP")
    List<LoginAttemptJpaEntity> findAttemptsToUnblock();
    
    @Query("SELECT COUNT(l) FROM LoginAttemptJpaEntity l WHERE l.userIdentifier = :userIdentifier " +
           "AND l.success = false AND l.attemptTimestamp >= :since")
    long countFailedAttemptsByUserAndPeriod(@Param("userIdentifier") String userIdentifier, 
                                          @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(l) FROM LoginAttemptJpaEntity l WHERE l.ipAddress = :ipAddress " +
           "AND l.success = false AND l.attemptTimestamp >= :since")
    long countFailedAttemptsByIpAndPeriod(@Param("ipAddress") String ipAddress, 
                                        @Param("since") LocalDateTime since);
    
    void deleteByAttemptTimestampBefore(LocalDateTime cutoffDate);
    
    void deleteByUserIdentifier(String userIdentifier);
}
