package com.petconnect.domain.user.port;

import com.petconnect.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void deleteById(UUID id);

    boolean existsByEmail(String email);
    Page<User> findAll(Pageable pageable);

    List<User> findByUserType(String userType);

    long countByUserType(String userType);
    User save(User user);
}