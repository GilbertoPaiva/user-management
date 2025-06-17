package com.petconnect.infrastructure.adapter.persistence.adapter;

import com.petconnect.domain.user.entity.User;
import com.petconnect.domain.user.port.UserRepositoryPort;
import com.petconnect.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.petconnect.infrastructure.adapter.persistence.mapper.UserMapper;
import com.petconnect.infrastructure.adapter.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = userMapper.toJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);
        return userMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userMapper::toDomainEntity);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userJpaRepository.findAll(pageable)
                .map(userMapper::toDomainEntity);
    }

    @Override
    public List<User> findByUserType(String userType) {
        List<UUID> userIds = userJpaRepository.findIdsByUserType(com.petconnect.domain.user.entity.UserType.valueOf(userType));
        return userIds.stream()
                .map(id -> userJpaRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(userMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserType(String userType) {
        return userJpaRepository.countByUserType(com.petconnect.domain.user.entity.UserType.valueOf(userType));
    }

    @Override
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
