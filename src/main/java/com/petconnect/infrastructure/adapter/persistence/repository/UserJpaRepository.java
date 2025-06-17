package com.petconnect.infrastructure.adapter.persistence.repository;

import com.petconnect.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.petconnect.domain.user.entity.UserType;
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
    
    @Query("SELECT u FROM UserJpaEntity u WHERE u.username = :username")
    Optional<UserJpaEntity> findByUsername(@Param("username") String username);
    
    @Query("SELECT u FROM UserJpaEntity u WHERE u.email = :email")
    Optional<UserJpaEntity> findByEmail(@Param("email") String email);
    
    @Query("SELECT u.id FROM UserJpaEntity u WHERE u.userType = :userType")
    List<UUID> findIdsByUserType(@Param("userType") UserType userType);
    
    @Query("SELECT CASE WHEN COUNT(u.id) > 0 THEN true ELSE false END FROM UserJpaEntity u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
    
    @Query("SELECT CASE WHEN COUNT(u.id) > 0 THEN true ELSE false END FROM UserJpaEntity u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(u.id) FROM UserJpaEntity u WHERE u.userType = :userType")
    long countByUserType(@Param("userType") UserType userType);
    
    @Query("SELECT u FROM UserJpaEntity u WHERE " +
           "(:name IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:userType IS NULL OR u.userType = :userType)")
    Page<UserJpaEntity> findByNameAndUserType(@Param("name") String name, 
                                            @Param("userType") UserType userType, 
                                            Pageable pageable);
}
