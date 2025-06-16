package com.userauth.usermanagement.infrastructure.adapter.persistence.entity;

import com.userauth.usermanagement.domain.user.entity.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;
    
    @Column(name = "security_question_1")
    private String securityQuestion1;
    
    @Column(name = "security_answer_1")
    private String securityAnswer1;
    
    @Column(name = "security_question_2")
    private String securityQuestion2;
    
    @Column(name = "security_answer_2")
    private String securityAnswer2;
    
    @Column(name = "security_question_3")
    private String securityQuestion3;
    
    @Column(name = "security_answer_3")
    private String securityAnswer3;
    
    private String nome;
    private String location;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    private String cnpj;
    private String crmv;
    
    @Column(name = "store_type")
    private String storeType;
    
    @Column(name = "business_hours")
    private String businessHours;
    
    private String guardian;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
