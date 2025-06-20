package com.petconnect.infrastructure.adapter.persistence.entity;

import com.petconnect.domain.shared.entity.AuditableEntity;
import com.petconnect.domain.user.entity.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "roles")
@ToString(exclude = "roles")
public class UserJpaEntity extends AuditableEntity {
    
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
    @Builder.Default
    private Boolean active = true;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleJpaEntity> roles;
    
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
}
