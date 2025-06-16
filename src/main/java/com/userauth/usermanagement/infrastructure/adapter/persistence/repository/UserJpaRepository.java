package com.userauth.usermanagement.infrastructure.adapter.persistence.repository;

import com.userauth.usermanagement.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.userauth.usermanagement.domain.user.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    
    Optional<UserJpaEntity> findByUsername(String username);
    Optional<UserJpaEntity> findByEmail(String email);
    List<UserJpaEntity> findByUserType(UserType userType);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM UserJpaEntity u WHERE u.userType = :userType")
    long countByUserType(@Param("userType") UserType userType);
    
    @Query("SELECT u FROM UserJpaEntity u WHERE " +
           "(:name IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:userType IS NULL OR u.userType = :userType)")
    Page<UserJpaEntity> findByNameAndUserType(@Param("name") String name, 
                                            @Param("userType") UserType userType, 
                                            Pageable pageable);
}
