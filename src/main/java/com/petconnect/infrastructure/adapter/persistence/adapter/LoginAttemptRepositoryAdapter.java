package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.security.entity.LoginAttempt;
import com.petconnect.domain.security.port.LoginAttemptRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.mapper.LoginAttemptMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.LoginAttemptJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do repositório de tentativas de login
 */
@Repository
@RequiredArgsConstructor
@Transactional
public class LoginAttemptRepositoryAdapter implements LoginAttemptRepositoryPort {
    
    private final LoginAttemptJpaRepository jpaRepository;
    private final LoginAttemptMapper mapper;
    
    @Override
    public LoginAttempt save(LoginAttempt loginAttempt) {
        var jpaEntity = mapper.toJpaEntity(loginAttempt);
        var savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LoginAttempt> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LoginAttempt> findByUserIdentifierAndIpAddress(String userIdentifier, String ipAddress) {
        return jpaRepository.findByUserIdentifierAndIpAddress(userIdentifier, ipAddress)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findByUserIdentifier(String userIdentifier) {
        return jpaRepository.findByUserIdentifier(userIdentifier)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findByIpAddress(String ipAddress) {
        return jpaRepository.findByIpAddress(ipAddress)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findRecentAttemptsByUser(String userIdentifier, LocalDateTime since) {
        return jpaRepository.findRecentAttemptsByUser(userIdentifier, since)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findBlockedAttempts() {
        return jpaRepository.findBlockedAttempts()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findAttemptsToUnblock() {
        return jpaRepository.findAttemptsToUnblock()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countFailedAttemptsByUserAndPeriod(String userIdentifier, LocalDateTime since) {
        return jpaRepository.countFailedAttemptsByUserAndPeriod(userIdentifier, since);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countFailedAttemptsByIpAndPeriod(String ipAddress, LocalDateTime since) {
        return jpaRepository.countFailedAttemptsByIpAndPeriod(ipAddress, since);
    }
    
    @Override
    public void deleteAttemptsOlderThan(LocalDateTime cutoffDate) {
        jpaRepository.deleteByAttemptTimestampBefore(cutoffDate);
    }
    
    @Override
    public void deleteByUserIdentifier(String userIdentifier) {
        jpaRepository.deleteByUserIdentifier(userIdentifier);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LoginAttempt> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
